package services

import javax.inject.{ Inject, Singleton }

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.util.parsing.json.JSON

import play.api.Configuration
import play.api.i18n.Messages
import play.api.libs.json.{ JsValue, Json, Reads }
import play.api.mvc.{ Result, Results }
import org.slf4j.LoggerFactory

import config.Properties
import utils.UriBuilder.createUri
import uk.gov.ons.sbr.models._

@Singleton
class SearchService @Inject() (val configuration: Configuration) extends Properties with Results {

  private[this] val logger = LoggerFactory.getLogger(getClass.getName)

  // split into function and avoid using this
  protected type UnitLinksListType = Seq[JsValue]
  protected type StatisticalUnitLinkType = JsValue

  val CAPPED_DISPLAY_NUMBER = 1

  //  // @ TODO - CHECK error control
  //  protected def search[T](key: String, baseUrl: String, sourceType: DataSourceTypes = ENT,
  //                          periodParam: Option[String] = None, history: Option[Int] = None)(implicit
  //                                                                                           fjs: Reads[T],
  //                                                                                           ws: RequestGenerator): Future[Result] = {
  //    key match {
  //      case k if k.length >= MINIMUM_KEY_LENGTH =>
  //        logger.debug(s"Sending request to ${baseUrl.toString} to retrieve Unit Links")
  //        ws.singleGETRequest(baseUrl.toString) map {
  //          case response if response.status == OK => {
  //            logger.debug(s"Result for unit is: ${response.body}")
  //            // @ TODO - add to success or failure to JSON ??
  //            val unitResp = response.json.as[T]
  //            unitResp match {
  //              case u: UnitLinksListType =>
  //                // if one UnitLinks found -> get unit
  //                if (u.length == CAPPED_DISPLAY_NUMBER) {
  //                  val id = (u.head \ "id").as[String]
  //                  logger.debug(s"Found a single response with $id")
  //                  val mapOfRecordKeys = Map((u.head \ "unitType").as[String] -> id)
  //                  val respRecords = parsedRequest(mapOfRecordKeys, periodParam, history)
  //                  val json: Seq[JsValue] = (u zip respRecords).map(x => toJson(x, (u.head \ "unitType").as[String]))
  //                  Ok(Json.toJson(json)).as(JSON)
  //                } else {
  //                  logger.debug(s"Found multiple records matching given id, $key. Returning multiple as list.")
  //                  // return UnitLinks if multiple
  //                  PartialContent(unitResp.toString).as(JSON)
  //                }
  //              case s: StatisticalUnitLinkType =>
  //                val mapOfRecordKeys = Map(sourceType.toString -> (s \ "id").as[String])
  //                val respRecords = parsedRequest(mapOfRecordKeys, periodParam)
  //                val json = (Seq(s) zip respRecords).map(x => toJson(x, sourceType.toString)).head
  //                Ok(json).as(JSON)
  //            }
  //          }
  //          case response if response.status == NOT_FOUND => NotFound(response.body).as(JSON)
  //        } recover responseException
  //      case _ =>
  //        BadRequest(Messages("controller.invalid.id", key, MINIMUM_KEY_LENGTH)).future
  //    }
  //  }

  private def parsedRequest(searchList: Map[String, String], withPeriod: Option[String] = None,
    limit: Option[Int] = None)(implicit ws: RequestGenerator): List[JsValue] = {
    searchList.map {
      case (group, id) =>
        val unit = DataSourceTypesUtil.fromString(group)
        val path = unit match {
          case Some(LEU) => LEGAL_UNIT_DATA_API_URL
          case Some(CRN) => CH_ADMIN_DATA_API_URL
          case Some(VAT) => VAT_ADMIN_DATA_API_URL
          case Some(PAYE) => PAYE_ADMIN_DATA_API_URL
          case Some(ENT) => SBR_CONTROL_API_URL
        }
        // TODO - fix unit.getOrElse("").toString
        val newPath = createUri(path, id, withPeriod, group = unit.getOrElse("").toString, history = limit)
        logger.info(s"Sending request to $newPath to get records of all variables of unit.")
        // @TODO - Duration.Inf -> place cap
        val resp = ws.singleGETRequestWithTimeout(newPath.toString, Duration.Inf)
        logger.debug(s"Result for record is: ${resp.body}")
        // @ TODO - add to success or failrue to JSON ??
        resp.json
    }.toList
  }

}
