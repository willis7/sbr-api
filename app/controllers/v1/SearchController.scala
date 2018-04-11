package controllers.v1

import java.time.format.DateTimeParseException
import javax.inject.{ Inject, Singleton }
import javax.naming.ServiceUnavailableException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Future, TimeoutException }
import scala.util.Try

import play.api.Configuration
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.libs.json.{ JsResultException, Reads }
import play.api.libs.ws.WSResponse
import play.api.mvc._
import org.slf4j.LoggerFactory
import io.swagger.annotations.Api

import uk.gov.ons.sbr._
import uk.gov.ons.sbr.models._

import utils.FutureResponse.futureSuccess
import utils.Utilities.errAsJson
import services.{ RequestGenerator, SearchService }
import swagger.SearchControllerSwagger

/**
 * SearchController
 * ----------------
 * Author: haqa
 * Date: 10 July 2017 - 09:25
 * Copyright (c) 2017  Office for National Statistics
 */

@Api("Search")
@Singleton
class SearchController @Inject() (
    implicit
    ws: RequestGenerator,
    val configuration: Configuration,
    val messagesApi: MessagesApi
) extends Controller with SearchControllerSwagger with I18nSupport {

  private[this] val logger = LoggerFactory.getLogger(getClass.getName)
  private val searchService = new SearchService

  type UnitLinksList = Seq[UnitLinks]
  type StatisticalUnit = UnitLinks

  def searchById(id: Option[String], history: Option[Int]): Action[AnyContent] = {
    Action.async { implicit request =>
      // TODO investigate why the use of getQueryString
      val key = id.orElse(request.getQueryString("id")).getOrElse("")
      val limit = history.orElse(Try(Some(request.getQueryString("history").get.toInt)).getOrElse(None))
      logger.info(s"Sending request to Control Api to identify $key")
      search(key, limit)
    }
  }

  def searchByPeriod(id: Option[String], period: String): Action[AnyContent] = {
    Action.async { implicit request =>
      // TODO investigate why the use of getQueryString
      val key = id.orElse(request.getQueryString("id")).getOrElse("")
      logger.info(s"Sending request to Control Api to identify $key with $period")
      searchWithPeriod[UnitLinksList](key, Some(period))
    }
  }

  // TODO - Add swagger to other routes and updated ApiResponses annotation
  def searchLeu(period: String, id: String): Action[AnyContent] = Action.async {
    logger.info(s"Sending request to Control Api to retrieve legal unit with $id and $period")
    searchWithPeriod[StatisticalUnit](id, Some(period), Some(LEU))
  }

  def searchEnterprise(period: String, id: String): Action[AnyContent] = Action.async {
    logger.info(s"Sending request to Control Api to retrieve enterprise with $id and $period")
    searchWithPeriod[StatisticalUnit](id, Some(period), Some(ENT))
  }

  def searchVat(period: String, id: String): Action[AnyContent] = Action.async {
    logger.info(s"Sending request to Admin Api to retrieve VAT reference with $id and $period")
    searchWithPeriod[StatisticalUnit](id, Some(period), Some(VAT))
  }

  def searchPaye(period: String, id: String): Action[AnyContent] = Action.async {
    logger.info(s"Sending request to Admin Api to retrieve PAYE record with $id and $period")
    searchWithPeriod[StatisticalUnit](id, Some(period), Some(PAYE))
  }

  def searchCrn(period: String, id: String): Action[AnyContent] = Action.async {
    logger.info(s"Sending request to Admin Api to retrieve Companies House Number with $id and $period")
    searchWithPeriod[StatisticalUnit](id, Some(period), Some(CRN))
  }

  def search(key: String, history: Option[Int]): Future[Result] =
    //  def search(key: String, history: Option[Int])(implicit reads: Reads[UnitLinksList]): Future[Result] =
    RequestEvaluationUtils.matchByParams(key).fold(
      e => BadRequest(e.getMessage).future,
      // TODO investigate why the use of getQueryString
      i => {
        history.fold(searchService.generateRequest[UnitLinksList](IdRequest(i.id)))(
          h =>
            searchService.generateRequest[UnitLinksList](IdHistoryRequest(i.id, h))
        ).map {
            //        val ul = implicitly[UnitLinksType[UnitLinksList]]
            //        searchService.generateRequest[UnitLinksList](i.id, None, None, history)(reads, ul).map {
            case Right(response: WSResponse) if response.status == OK => Ok(response.json).as(JSON)
            case Right(response: WSResponse) if response.status == NOT_FOUND => NotFound(response.json).as(JSON)
            case Left(ex: Throwable) => responseException(ex)
          }
      }
    )

  def searchWithPeriod[T](id: String, period: Option[String] = None, `type`: Option[DataSourceTypes] = None)(implicit reads: Reads[T]): Future[Result] =
    RequestEvaluationUtils.getUnitMatchRequest(id, period) match {
      case Right(r: RequestEvaluation) =>
        searchService.generateRequest[T](r)(reads).map {
          //        val ul = implicitly[UnitLinksType[T]]
          //        searchService.generateRequest[T](id, Some(r.period), `type`)(reads, ul).map {
          case Right(response: WSResponse) if response.status == OK => Ok(response.json).as(JSON)
          case Right(response: WSResponse) if response.status == NOT_FOUND => NotFound(response.json).as(JSON)
          case Left(ex: Throwable) => responseException(ex)
        }
      case Left(ex: DateTimeParseException) =>
        BadRequest(Messages("controller.datetime.failed.parse", ex.toString,
          RequestEvaluationUtils.yearMonthFormat)).future
      case Left(e: InvalidKeyException) => BadRequest(e.getMessage).future
    }

  def responseException(e: Throwable): Result =
    e match {
      case ex: DateTimeParseException =>
        logger.error("controller.datetime.failed.parse", ex)
        BadRequest(Messages("controller.datetime.failed.parse", ex.toString))
      case ex: JsResultException =>
        logger.error(s"Cannot parse json to object with exception $ex", ex)
        InternalServerError(s"$ex")
      case ex: RuntimeException =>
        logger.error("Error, something went wrong", ex)
        InternalServerError(s"$ex")
      case ex: ServiceUnavailableException =>
        logger.error(s"Exception caught due to an internal service failure or not found - ${ex.getCause}", ex)
        ServiceUnavailable(errAsJson(ex.toString, ex.getCause.toString))
      case ex: TimeoutException =>
        logger.error(s"Failure due to service timeout by ${ex.getCause.toString}", ex)
        RequestTimeout(Messages("controller.timeout.request", s"$ex", s"${ex.getCause}"))
      case ex =>
        logger.error(s"Unexpected error caused by some exception ${ex.getClass.toString}", ex)
        InternalServerError(errAsJson(ex.toString, ex.getCause.toString))
    }
}
