package controllers.v1

import java.time.format.DateTimeParseException
import javax.naming.ServiceUnavailableException

import play.api.mvc.{ Controller, Result }
import com.typesafe.scalalogging.StrictLogging
import play.api.libs.json.{ JsValue, Json }
import utils.Utilities.{ errAsJson, orElseNull }

import scala.concurrent.TimeoutException

/**
 * Created by haqa on 10/07/2017.
 */
trait ControllerUtils extends Controller with StrictLogging {

  protected val placeholderPeriod = "*date"
  // number of units displayable
  protected val cappedDisplayNumber = 1
  protected val fixedYeaMonthSize = 6

  protected def seqToJson(record: Seq[JsValue], links: Seq[JsValue]): JsValue = {
    val res = (links zip record).map(
      z => toJson(z._2, z._1)
    )
    Json.toJson(res)
  }

  protected def toJson(record: JsValue, links: JsValue): JsValue = {
    // For BI, there is no "vars", just use the whole record
    val vars = (record \ "vars").getOrElse(record)
    // BI does not have period, so use an empty string
    val period = (record \ "period").getOrNull

    // Only get childrenJson for Enterprises
    val js = (links \ "unitType").getOrNull.toString.replaceAll("^\"|\"$", "") match {
      case "ENT" => {
        Json.obj(
          "id" -> (links \ "id").getOrNull,
          "parents" -> (links \ "parents").getOrNull,
          "children" -> (links \ "children").getOrNull,
          "childrenJson" -> (record \ "childrenJson").getOrNull,
          "unitType" -> (links \ "unitType").getOrNull,
          "period" -> period,
          "vars" -> vars
        )
      }
      case _ => Json.obj(
        "id" -> (links \ "id").getOrNull,
        "parents" -> (links \ "parents").getOrNull,
        "children" -> (links \ "children").getOrNull,
        "unitType" -> (links \ "unitType").getOrNull,
        "period" -> period,
        "vars" -> vars
      )
    }
    Json.toJson(js)
  }

  protected def responseException: PartialFunction[Throwable, Result] = {
    case ex: DateTimeParseException =>
      BadRequest(errAsJson(BAD_REQUEST, "invalid_date", s"cannot parse date exception found $ex"))
    case ex: RuntimeException =>
      InternalServerError(errAsJson(INTERNAL_SERVER_ERROR, "runtime_exception", s"$ex", s"${ex.getCause}"))
    case ex: ServiceUnavailableException =>
      ServiceUnavailable(errAsJson(SERVICE_UNAVAILABLE, "service_unavailable", s"$ex", s"${ex.getCause}"))
    case ex: TimeoutException =>
      RequestTimeout(errAsJson(REQUEST_TIMEOUT, "request_timeout",
        s"This may be due to connection being blocked or host failure. Found exception $ex", s"${ex.getCause}"))
    case ex => InternalServerError(errAsJson(INTERNAL_SERVER_ERROR, "internal_server_error", s"$ex", s"${ex.getCause}"))
  }

}
