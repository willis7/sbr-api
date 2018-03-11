package controllers.v1

import java.time.YearMonth
import java.time.format.{DateTimeFormatter, DateTimeParseException}
import javax.inject.{Inject, Singleton}
import javax.naming.ServiceUnavailableException

import scala.concurrent.TimeoutException
import scala.util.{Failure, Success, Try}

import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.JsValue
import play.api.mvc.{AnyContent, Request, Result, Results}
import org.slf4j.{Logger, LoggerFactory}

import config.Properties
import utils.Utilities.yearMonthFormat
import utils.Utilities.errAsJson
import uk.gov.ons.sbr.models._

@Singleton
class ControllerUtils @Inject() (val messagesApi: MessagesApi, val configuration: Configuration)
  extends Results with I18nSupport with Properties {

  val PLACEHOLDER_PERIOD = "*date"
  val PLACEHOLDER_UNIT_TYPE = "*type"
  // number of units displayable
  val CAPPED_DISPLAY_NUMBER = 1
  val FIXED_YEARMONTH_SIZE = 6

  protected[this] val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  protected type UnitLinksListType = Seq[JsValue]
  protected type StatisticalUnitLinkType = JsValue

  private def responseException: PartialFunction[Throwable, Result] = {
    case ex: DateTimeParseException =>
      BadRequest(Messages("controller.datetime.failed.parse", ex.toString))
    case ex: RuntimeException =>
      InternalServerError(errAsJson(ex.toString, ex.getCause.toString))
    case ex: ServiceUnavailableException =>
      ServiceUnavailable(errAsJson(ex.toString, ex.getCause.toString))
    case ex: TimeoutException =>
      RequestTimeout(Messages("controller.timeout.request", s"$ex", s"${ex.getCause}"))
    case ex => InternalServerError(errAsJson(ex.toString, ex.getCause.toString))
  }

  protected def validateYearMonth(key: String, raw: String): RequestEvaluation = {
    val yearAndMonth = Try(YearMonth.parse(raw, DateTimeFormatter.ofPattern(yearMonthFormat)))
    yearAndMonth match {
      case Success(s) =>
        ReferencePeriod(key, s)
      case Failure(ex: DateTimeParseException) =>
        logger.error("cannot parse date to YearMonth object", ex)
        InvalidReferencePeriod(key, ex)
    }
  }

  def matchByParams(key: String, date: Option[String] = None): RequestEvaluation = {
//    val key = id.orElse(request.getQueryString("id")).getOrElse("")
    if (key.length >= MINIMUM_KEY_LENGTH) {
      date match {
        case None => IdRequest(key)
        case Some(s) => validateYearMonth(key, s)
      }
    } else { InvalidKey(key) }
  }

}
