package controllers.v1

import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import play.api.i18n.MessagesApi
import play.api.libs.json._
import play.api.mvc.{ Action, AnyContent, Controller, Result }
import play.api.{ Configuration, Logger }
import io.swagger.annotations.Api

import config.Properties
import utils.FutureResponse.futureSuccess
import utils.Utilities._
import services.RequestGenerator
import swagger.EditControllerSwagger

// TODO - Add proper exception and other RESULT type Handling
@Api("Edit")
class EditController @Inject() (ws: RequestGenerator, val configuration: Configuration, val messagesApi: MessagesApi) extends Controller with Properties with EditControllerSwagger {
  // TODO: Fix CORS issue to allow use of Content-Type: application/json
  // There is a CORS issue meaning the UI cannot do a POST request with the headers:
  // Content-Type: application/json
  // There is a temporary fix below, to just parse the POST body as text and do Json.parse(text).
  private val utils = new ControllerUtils(messagesApi)

  def editEnterprise(id: String): Action[AnyContent] = Action.async { implicit request =>
    val url = s"$CONTROL_EDIT_ENTERPRISE_URL$id"
    val jsonBody: Option[String] = request.body.asText
    Logger.info(s"Rerouting edit enterprise by default period request to: $url")
    rerouteEditPost(jsonBody, url)
  }

  def editEnterpriseForPeriod(period: String, id: String): Action[AnyContent] = Action.async { implicit request =>
    val url = s"${CONTROL_EDIT_ENTERPRISE_URL.replace(utils.PLACEHOLDER_PERIOD, period)}$id"
    val jsonBody: Option[String] = request.body.asText
    Logger.info(s"Rerouting edit enterprise by specified period request to: $url")
    rerouteEditPost(jsonBody, url)
  }

  def rerouteEditPost(jsonBody: Option[String], url: String): Future[Result] = {
    jsonBody.map { text =>
      ws.controlReroute(url, "Content-Type" -> "application/json", Json.parse(text.toString)).map {
        response => Status(response.status)(response.body)
      }
    }.getOrElse {
      Logger.debug(s"Invalid JSON for redirect to url: $url")
      BadRequest(errAsJson(BAD_REQUEST, "invalid_json", "POST body json is malformed", "Not Traced")).future
    }
  }
}
