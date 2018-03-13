package controllers.v1

import java.time.format.DateTimeParseException
import javax.inject.{Inject, Singleton}

import scala.concurrent.Future
import scala.util.Try

import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, Result}
import io.swagger.annotations._

import uk.gov.ons.sbr.models._

import utils.FutureResponse.futureSuccess
import services.RequestGenerator
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
   implicit ws: RequestGenerator,
   val configuration: Configuration,
   val messagesApi: MessagesApi) extends ControllerHelper with SearchControllerSwagger with I18nSupport {

  // TODO - updated ApiResponses annotation
//  //public api
//  def searchByIdOLD(id: Option[String], history: Option[Int]): Action[AnyContent] = {
//    Action.async { implicit request =>
//      val key = id.orElse(request.getQueryString("id")).getOrElse("")
//      val limit = history.orElse(Try(Some(request.getQueryString("history").get.toInt)).getOrElse(None))
//      val uri = createUri(SBR_CONTROL_API_URL, key)
//      search[UnitLinksListType](key, uri, history = limit)
//    }
//  }

  // TODO - updated ApiResponses annotation
  //public api
  //  def searchByReferencePeriodOLD(id: Option[String], period: String): Action[AnyContent] = {
  //    Action.async { implicit request =>
  //      val key = id.orElse(request.getQueryString("id")).getOrElse("")
  //      val res = period match {
  //        case x if x.length == FIXED_YEARMONTH_SIZE =>
  //          val uri = createUri(SBR_CONTROL_API_URL, key, Some(period))
  //          search[UnitLinksListType](key, uri, periodParam = Some(period))
  //        case _ => BadRequest(Messages("controller.invalid.period", period, "yyyyMM")).future
  //      }
  //      res
  //    }
  //  }

  def searchById(id: Option[String], history: Option[Int]): Action[AnyContent] = {
    Action.async { implicit request =>
      val key = id.orElse(request.getQueryString("id")).getOrElse("")
      LOGGER.info(s"Sending request to Control Api to identify $key")
      RequestEvaluationUtils.matchByParams(key) match {
        case Right(v: IdRequest) =>
          val limit = history.orElse(Try(Some(request.getQueryString("history").get.toInt)).getOrElse(None))
//          val uri = createUri(SBR_CONTROL_API_URL, key)
//          search[UnitLinksListType](key, uri, history = limit)
          ???
          searchWithPeriod[UnitLinksListType](key, history = limit)
        case Left(e: InvalidKeyException) => BadRequest(e).future
      }
    }
  }

  def searchByReferencePeriod(id: Option[String], period: String): Action[AnyContent] = {
    Action.async { implicit request =>
      val key = id.orElse(request.getQueryString("id")).getOrElse("")
      LOGGER.info(s"Sending request to Control Api to identify $key with $period")
      searchWithPeriod[UnitLinksListType](key, Some(period))
    }
  }


  // TODO - Add swagger to other routes and updated ApiResponses annotation
  def searchLeu(period: String, id: String): Action[AnyContent] = Action.async {
    LOGGER.info(s"Sending request to Control Api to retrieve legal unit with $id and $period")
//    val uri = createUri(SBR_CONTROL_API_URL, id, Some(period), Some(LEU))
//    search[StatisticalUnitLinkType](id, uri, LEU, Some(period))
    searchWithPeriod[StatisticalUnitLinkType](id, Some(period), Some(LEU))
  }

  def searchEnterprise(period: String, id: String): Action[AnyContent] = Action.async {
    LOGGER.info(s"Sending request to Control Api to retrieve enterprise with $id and $period")
//    val uri = createUri(SBR_CONTROL_API_URL, id, Some(period), Some(ENT))
//    search[StatisticalUnitLinkType](id, uri, ENT, Some(period))
    searchWithPeriod[StatisticalUnitLinkType](id, Some(period), Some(ENT))
  }

  def searchVat(period: String, id: String): Action[AnyContent] = Action.async {
    LOGGER.info(s"Sending request to Admin Api to retrieve VAT reference with $id and $period")
//    val uri = createUri(SBR_CONTROL_API_URL, id, Some(period), Some(VAT))
//    search[StatisticalUnitLinkType](id, uri, VAT, Some(period))
    searchWithPeriod[StatisticalUnitLinkType](id, Some(period), Some(VAT))
  }

  def searchPaye(period: String, id: String): Action[AnyContent] = Action.async {
    LOGGER.info(s"Sending request to Admin Api to retrieve PAYE record with $id and $period")
//    val uri = createUri(SBR_CONTROL_API_URL, id, Some(period), Some(PAYE))
//    search[StatisticalUnitLinkType](id, uri, PAYE, Some(period))
    searchWithPeriod[StatisticalUnitLinkType](id, Some(period), Some(PAYE))
  }

  def searchCrn(period: String, id: String): Action[AnyContent] = Action.async {
    LOGGER.info(s"Sending request to Admin Api to retrieve Companies House Number with $id and $period")
//    val uri = createUri(SBR_CONTROL_API_URL, id, Some(period), Some(CRN))
//    search[StatisticalUnitLinkType](id, uri, CRN, Some(period))
    searchWithPeriod[StatisticalUnitLinkType](id, Some(period), Some(CRN))
  }

  def searchWithPeriod[T](key: String, period: Option[String] = None, `type`: Option[DataSourceTypes] = None,
                          history: Option[Int] = None): Future[Result] = {
    RequestEvaluationUtils.matchByParams(key, period) match {
      case Right(r: ReferencePeriod) =>
        search[T](key, `type`, period, history)
//        val uri = createUri(SBR_CONTROL_API_URL, key, period)
//        search[UnitLinksListType](key, uri, periodParam = period)
//        ???
      case Left(ex: DateTimeParseException) =>
        BadRequest(Messages("controller.datetime.failed.parse", ex.toString, RequestEvaluationUtils.yearMonthFormat)).future
      case Left(e: InvalidKeyException) => BadRequest(e).future
    }
  }
}
