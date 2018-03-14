package controllers.v1

import java.time.format.DateTimeParseException
import javax.inject.{ Inject, Singleton }
import javax.naming.ServiceUnavailableException

import scala.concurrent.TimeoutException

import play.api.Configuration
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.libs.json.JsValue
import play.api.mvc.{ Result, Results }
import org.slf4j.{ Logger, LoggerFactory }

import utils.Utilities.errAsJson

@Singleton
class ControllerUtils @Inject() (val messagesApi: MessagesApi, val configuration: Configuration)
    extends Results with I18nSupport {

  val PLACEHOLDER_PERIOD = "*date"
  val PLACEHOLDER_UNIT_TYPE = "*type"
  // number of units displayable
  val CAPPED_DISPLAY_NUMBER = 1
  val FIXED_YEARMONTH_SIZE = 6

  protected[this] val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  protected type UnitLinksListType = Seq[JsValue]
  protected type StatisticalUnitLinkType = JsValue

  def responseException: PartialFunction[Throwable, Result] = {
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

}
