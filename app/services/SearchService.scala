package services

import java.time.YearMonth
import javax.inject.{Inject, Singleton}

import scala.concurrent.Future

import play.api.Configuration
import play.api.http.Status
import play.api.libs.json.{JsResultException, JsValue}
import play.api.libs.ws.WSResponse
import play.api.mvc.Results
import org.slf4j.LoggerFactory

import uk.gov.ons.sbr.models._

import config.Properties
import utils.UriBuilder.createUri

@Singleton
class SearchService @Inject() (implicit ws: RequestGenerator, val configuration: Configuration) extends Status with Results {

  private[this] val logger = LoggerFactory.getLogger(getClass.getName)

  private val props = new Properties(configuration)

  // split into function and avoid using this
  type UnitLinksListType = Seq[JsValue]
  type StatisticalUnitLinkType = JsValue
  protected val PLACEHOLDER_PERIOD = "*date"
  private val PLACEHOLDER_UNIT_TYPE = "*type"
  // number of units displayable
  protected val FIXED_YEARMONTH_SIZE = 6
  val CAPPED_DISPLAY_NUMBER = 1

  private val findUrl: (DataSourceTypes) => String = getUrl(props)

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

  //  private def parsedRequest(searchList: Map[String, String], withPeriod: Option[String] = None,
  //    limit: Option[Int] = None)(implicit ws: RequestGenerator): List[JsValue] = {
  //    searchList.map {
  //      case (group, id) =>
  //        val unit = DataSourceTypesUtil.fromString(group)
  //        val path = unit match {
  //          case Some(LEU) => LEGAL_UNIT_DATA_API_URL
  //          case Some(CRN) => CH_ADMIN_DATA_API_URL
  //          case Some(VAT) => VAT_ADMIN_DATA_API_URL
  //          case Some(PAYE) => PAYE_ADMIN_DATA_API_URL
  //          case Some(ENT) => SBR_CONTROL_API_URL
  //        }
  //        // TODO - fix unit.getOrElse("").toString
  //        val newPath = createUri(path, id, withPeriod, group = unit.getOrElse("").toString, history = limit)
  //        logger.info(s"Sending request to $newPath to get records of all variables of unit.")
  //        // @TODO - Duration.Inf -> place cap
  //        val resp = ws.singleGETRequestWithTimeout(newPath.toString, Duration.Inf)
  //        logger.debug(s"Result for record is: ${resp.body}")
  //        // @ TODO - add to success or failrue to JSON ??
  //        resp.json
  //    }.toList
  //  }

  def validateJsonParse[T](json: JsValue): Either[Exception, T] = {
    json.validate[T].fold(
      invalid = err => Left(JsResultException(err)),
      valid = v => Right(v)
    )
  }

//  def validateJsonParse2[T](json: JsValue): Either[Exception, T] = {
//    json.validate[T].match{
//    case (s: JsSuccess[T]) => Right(s.get)
//
//    case (e: JsError) => new JsResultException("")
//      val g = e.
//      g
//      Left(e.errors)
//    }
//  }

  def searchRequest(id: String, period: Option[YearMonth] = None, `type`: Option[DataSourceTypes] = None,
    history: Option[Int] = None): Future[Either[Throwable, WSResponse]] = {
    getUnitLinks(id, period, `type`).map{
      case response if response.status == OK => {
        // @ TODO - ADD type class to control different UnitLink request -> UnitLinksListType AND StatisticalUnitLinkType
        validateJsonParse[Seq[JsValue]](response.json) match {
          case Right(js) =>
            if (js.length == CAPPED_DISPLAY_NUMBER) {
              getUnitRecord
              /**
                * HERE WE INCLUDE THE CORE SEARCH REQUESTS
                */
              Right(???)
            } else {
              logger.debug(s"Found multiple records matching given id, $id. Returning multiple as list.")
              Right(response)
            }
          case Left(ex: JsResultException) => Left(ex)
        }
      } case response if response.status == NOT_FOUND => {
        // TODO add proper period parser
        logger.debug(s"Could find UnitLink for id $id and period ${period.getOrElse("None").toString}.")
        Right(response)
      }
      case ex: Throwable => Left(ex)
    }
  }


  private def getUnitLinks(id: String, period: Option[YearMonth] = None, `type`: Option[DataSourceTypes] = None
                  ): Future[WSResponse] = {
    val unitLinkRequestUrl = createUri(props.SBR_CONTROL_API_URL, id, period, `type`)
    logger.debug(s"Sending request to $unitLinkRequestUrl to retrieve Unit Links")
    ws.singleGETRequest(unitLinkRequestUrl)
  }

  // TODO make check that `type` is defined else NullException before func invoked
  private def getUnitRecord(id: String, period: Option[YearMonth] = None, `type`: DataSourceTypes,
    history: Option[Int] = None): Future[WSResponse] = {
    val path = findUrl(`type`)
    val dataRecordRequestUrl = createUri(path, id, period, Some(`type`), history = history)
    logger.info(s"Sending request to $dataRecordRequestUrl to get records of all variables of unit.")
    ws.singleGETRequest(dataRecordRequestUrl)
  }

}
