package controllers.v1

import javax.inject.Inject

import io.swagger.annotations._
import play.api.mvc.{ Action, AnyContent, Result }
import utils.Utilities.errAsJson
import utils.FutureResponse._

import config.Properties.minKeyLength
import play.api.libs.json.JsValue
import uk.gov.ons.sbr.models.UnitMatch
import services.WSRequest.RequestGenerator

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import config.Properties._

@Api("Search")
class SearchController @Inject() (ws: RequestGenerator) extends ControllerUtils {

  //public api
  @ApiOperation(
    value = "Json list of id matches",
    notes = "The matches can occur from any id field and multiple records can be matched",
    responseContainer = "JSONObject",
    code = 200,
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, response = classOf[UnitMatch], responseContainer = "JSONObject", message = "Success -> Record(s) found for id."),
    new ApiResponse(code = 400, responseContainer = "JSONObject", message = "Client Side Error -> Required parameter was not found."),
    new ApiResponse(code = 404, responseContainer = "JSONObject", message = "Client Side Error -> Id not found."),
    new ApiResponse(code = 500, responseContainer = "JSONObject", message = "Server Side Error -> Request could not be completed.")
  ))
  def searchById(
    @ApiParam(value = "An identifier of any type", example = "825039145000", required = true) id: Option[String],
    @ApiParam(value = "term to categories the id source", required = false) origin: Option[String] = None
  ): Action[AnyContent] = {
    Action.async { implicit request =>
      val key = id.orElse(request.getQueryString("id"))
      //      val date = period.orElse(request.getQueryString("period"))
      val res: Future[Result] = key match {
        case Some(k) if k.length >= minKeyLength =>
          ws.singleRequest(k) map { response =>
            if (response.status == 200) {
              val unitResp = response.json.as[Seq[JsValue]]
              val mapOfRecordKeys = unitResp.map(x =>
                (x \ "unitType").as[String] -> (x \ "id").as[String]).toMap
              val respRecords: List[JsValue] = ws.multiRequest(mapOfRecordKeys)
              val json = (unitResp zip respRecords).map { case (u, e) => UnitMatch(u, e) }.toJson
              Ok(json).as(JSON)
            } else NotFound(response.body).as(JSON)
          } recover responseException
        case _ => BadRequest(errAsJson(BAD_REQUEST, "invalid_key_size", s"missing key or key is too short [$minKeyLength]")).future
      }
      res
    }
  }

  //public api
  @ApiOperation(
    value = "Json Object of matching legal unit",
    notes = "Sends request to Business Index for legal units",
    responseContainer = "JSONObject",
    code = 200,
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success - Displays json list of dates for official development."),
    new ApiResponse(code = 500, responseContainer = "Json", message = "Internal Server Error - Request timed-out."),
    new ApiResponse(code = 500, responseContainer = "Json", message = "Internal Server Error - Failed to connection or timeout with endpoint.")
  ))
  def searchByLeU(
     @ApiParam(value = "Identifier creation date", example = "2017/07", required = true) date: String,
     @ApiParam(value = "A legal unit identifier", example = "<some example>", required = true) id: String
  ): Action[AnyContent] = Action.async { request =>
    logger.info(s"Sending request to Business Index for legal unit: $id")
    search(id, businessIndexRoute)
  }

  def searchByEnterprise(
      @ApiParam(value = "Identifier creation date", example = "2017/07", required = true) date: String,
      @ApiParam(value = "A legal unit identifier", example = "<some example>", required = true) id: String
    ): Action[AnyContent] = Action.async { request =>
    logger.info(s"Sending request to Control Api to retrieve enterprise with $id and $date")
    search(id, controlEnterpriseSearchWithPeriod.replace(placeholderPeriod, date))
  }

  def searchByVat(
       @ApiParam(value = "Identifier creation date", example = "2017/07", required = true) date: String,
       @ApiParam(value = "A legal unit identifier", example = "<some example>", required = true) id: String
     ): Action[AnyContent] = Action.async { request =>
    logger.info(s"Sending request to Admin Api to retrieve VAT reference with $id and $date")
    search(id, adminVATsSearchWithPeriod.replace(placeholderPeriod, date))
  }

  def searchByPaye(
      @ApiParam(value = "Identifier creation date", example = "2017/07", required = true) date: String,
      @ApiParam(value = "A legal unit identifier", example = "<some example>", required = true) id: String
    ): Action[AnyContent] = Action.async { request =>
    logger.info(s"Sending request to Admin Api to retrieve PAYE record with $id and $date")
    search(id, adminPAYEsSearchWithPeriod.replace(placeholderPeriod, date))
  }

  def searchByCrn(
     @ApiParam(value = "Identifier creation date", example = "2017/07", required = true) date: String,
     @ApiParam(value = "A legal unit identifier", example = "<some example>", required = true) id: String
   ): Action[AnyContent] = Action.async { request =>
    logger.info(s"Sending request to Admin Api to retrieve Companies House Number with $id and $date")
    search(id, adminCompaniesSearchWithPeriod.replace(placeholderPeriod, date))
  }

  def searchByEnterpriseById(
    @ApiParam(value = "An identifier of any type", example = "825039145000", required = true) id: String
  ): Action[AnyContent] = {
    Action.async { request =>
      logger.info(s"Sending request to Control Api to retrieve enterprise with $id")
      search(id, controlEnterpriseSearch)
    }
  }

  def searchByVatById(
    @ApiParam(value = "A legal unit identifier", example = "<some example>", required = true) id: String
  ): Action[AnyContent] = Action.async { request =>
    logger.info(s"Sending request to Admin Api to retrieve VAT reference with $id")
    search(id, adminVATsSearch)
  }

  def searchByPayeById(
    @ApiParam(value = "A legal unit identifier", example = "<some example>", required = true) id: String
  ): Action[AnyContent] = Action.async { request =>
    logger.info(s"Sending request to Admin Api to retrieve PAYE record with $id")
    search(id, adminPAYEsSearch)
  }

  def searchByCrnById(
    @ApiParam(value = "A legal unit identifier", example = "<some example>", required = true) id: String
  ): Action[AnyContent] = Action.async { request =>
    logger.info(s"Sending request to Admin Api to retrieve Companies House Number with $id")
    search(id, adminCompaniesSearch)
  }

  protected def search(id: String, url: String) = {
    val res = id match {
      case id if id.length >= minKeyLength =>
        logger.info(s"Sending request to Business Index for legal unit id: $id")
        val resp = ws.singleRequestNoTimeout(s"$url$id") map { response =>
          if (response.status == 200) {
            Ok(response.body).as(JSON)
          } else NotFound(response.body).as(JSON)
        } recover responseException
        resp
      case _ => BadRequest(errAsJson(BAD_REQUEST, "missing_parameter", "No query string found")).future
    }
    res
  }

}
