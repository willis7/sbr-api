package controllers.v1

import javax.inject.{Inject, Singleton}

import scala.concurrent.Future
import scala.util.Try

import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, Result}
import io.swagger.annotations._

import utils.FutureResponse.futureSuccess
import utils.UriBuilder.createUri
import utils.Utilities.yearMonthFormat
import services.RequestGenerator
import swagger.SearchControllerSwagger
import uk.gov.ons.sbr.models._

/**
 * SearchController
 * ----------------
 * Author: haqa
 * Date: 10 July 2017 - 09:25
 * Copyright (c) 2017  Office for National Statistics
 */

@Api("Search")
@Singleton
class SearchController @Inject() (implicit ws: RequestGenerator, val configuration: Configuration,
    val messagesApi: MessagesApi) extends ControllerHelper with SearchControllerSwagger with I18nSupport {

  private val utilities = new ControllerUtils(messagesApi, configuration)

  // TODO - updated ApiResponses annotation
  //public api
  def searchByIdOLD(id: Option[String], history: Option[Int]): Action[AnyContent] = {
    Action.async { implicit request =>
      val key = id.orElse(request.getQueryString("id")).getOrElse("")
      val limit = history.orElse(Try(Some(request.getQueryString("history").get.toInt)).getOrElse(None))
      val uri = createUri(SBR_CONTROL_API_URL, key)
      search[UnitLinksListType](key, uri, history = limit)
    }
  }

  def searchById(id: Option[String], history: Option[Int]): Action[AnyContent] = {
    Action.async { implicit request =>
      val key = id.orElse(request.getQueryString("id")).getOrElse("")
      LOGGER.info(s"Sending request to Control Api to identify $key")
      utilities.matchByParams(key) match {
        case (v: IdRequest) =>
          val limit = history.orElse(Try(Some(request.getQueryString("history").get.toInt)).getOrElse(None))
          val uri = createUri(SBR_CONTROL_API_URL, key)
          search[UnitLinksListType](key, uri, history = limit)
          ???
        case (i: InvalidKey) => BadRequest(Messages("controller.invalid.id", i, MINIMUM_KEY_LENGTH)).future
      }
    }
  }

  // TODO - updated ApiResponses annotation
  //public api
  def searchByReferencePeriodOLD(id: Option[String], period: String): Action[AnyContent] = {
    Action.async { implicit request =>
      val key = id.orElse(request.getQueryString("id")).getOrElse("")
      val res = period match {
        case x if x.length == FIXED_YEARMONTH_SIZE =>
          val uri = createUri(SBR_CONTROL_API_URL, key, Some(period))
          search[UnitLinksListType](key, uri, periodParam = Some(period))
        case _ => BadRequest(Messages("controller.invalid.period", period, "yyyyMM")).future
      }
      res
    }
  }

  def searchByReferencePeriod(id: Option[String], period: String): Action[AnyContent] = {
    Action.async { implicit request =>
      val key = id.orElse(request.getQueryString("id")).getOrElse("")
      LOGGER.info(s"Sending request to Control Api to identify $key with $period")
      searchWithPeriod(key, Some(period))
    }
  }


  // TODO - Add swagger to other routes and updated ApiResponses annotation
  def searchLeu(period: String, id: String): Action[AnyContent] = Action.async {
    LOGGER.info(s"Sending request to Control Api to retrieve legal unit with $id and $period")
//    val uri = createUri(SBR_CONTROL_API_URL, id, Some(period), Some(LEU))
//    search[StatisticalUnitLinkType](id, uri, LEU, Some(period))
    searchWithPeriod(id, Some(period), Some(LEU))
  }

  def searchEnterprise(period: String, id: String): Action[AnyContent] = Action.async {
    LOGGER.info(s"Sending request to Control Api to retrieve enterprise with $id and $period")
//    val uri = createUri(SBR_CONTROL_API_URL, id, Some(period), Some(ENT))
//    search[StatisticalUnitLinkType](id, uri, ENT, Some(period))
    searchWithPeriod(id, Some(period), Some(ENT))
  }

  def searchVat(period: String, id: String): Action[AnyContent] = Action.async {
    LOGGER.info(s"Sending request to Admin Api to retrieve VAT reference with $id and $period")
//    val uri = createUri(SBR_CONTROL_API_URL, id, Some(period), Some(VAT))
//    search[StatisticalUnitLinkType](id, uri, VAT, Some(period))
    searchWithPeriod(id, Some(period), Some(VAT))
  }

  def searchPaye(period: String, id: String): Action[AnyContent] = Action.async {
    LOGGER.info(s"Sending request to Admin Api to retrieve PAYE record with $id and $period")
//    val uri = createUri(SBR_CONTROL_API_URL, id, Some(period), Some(PAYE))
//    search[StatisticalUnitLinkType](id, uri, PAYE, Some(period))
    searchWithPeriod(id, Some(period), Some(PAYE))
  }

  def searchCrn(period: String, id: String): Action[AnyContent] = Action.async {
    LOGGER.info(s"Sending request to Admin Api to retrieve Companies House Number with $id and $period")
//    val uri = createUri(SBR_CONTROL_API_URL, id, Some(period), Some(CRN))
//    search[StatisticalUnitLinkType](id, uri, CRN, Some(period))
    searchWithPeriod(id, Some(period), Some(CRN))
  }

  def searchWithPeriod(key: String, period: Option[String] = None, `type`: Option[DataSourceTypes] = None
                      ): Future[Result] = {
    utilities.matchByParams(key, period) match {
      case (r: ReferencePeriod) =>
//        val uri = createUri(SBR_CONTROL_API_URL, key, period)
//        search[UnitLinksListType](key, uri, periodParam = period)
        ???
      case (i: InvalidReferencePeriod) =>
        BadRequest(Messages("controller.datetime.failed.parse", i.exception, yearMonthFormat)).future
      case (i: InvalidKey) => BadRequest(Messages("controller.invalid.id", i.id, MINIMUM_KEY_LENGTH)).future
    }
  }
}
