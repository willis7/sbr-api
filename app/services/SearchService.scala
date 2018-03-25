package services

import javax.inject.{ Inject, Singleton }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import play.api.Configuration
import play.api.http.Status
import play.api.libs.json.{ JsResultException, Reads }
import play.api.libs.ws.WSResponse
import play.api.mvc.Results
import org.slf4j.LoggerFactory

import uk.gov.ons.sbr.models._
import uk.gov.ons.sbr.{ JsonParser, RequestEvaluation, UnitRequest }

import config.Properties
import utils.UriBuilder.createUri

@Singleton
class SearchService @Inject() (implicit ws: RequestGenerator, val configuration: Configuration) extends Status with Results {

  private[this] val logger = LoggerFactory.getLogger(getClass.getName)

  private val props = new Properties(configuration)

  // split into function and avoid using this
  protected val PLACEHOLDER_PERIOD = "*date"
  private val PLACEHOLDER_UNIT_TYPE = "*type"
  // number of units displayable
  protected val FIXED_YEARMONTH_SIZE = 6
  val CAPPED_DISPLAY_NUMBER = 1
  type FutureSearch = Future[Either[Throwable, WSResponse]]

  val findUrl: (DataSourceTypes) => String = DataSourceTypesUtil.getUrl(props)

  def generateRequest[T](requestEvaluation: RequestEvaluation)(implicit r: Reads[T]): FutureSearch = {
    getUnitLinks(requestEvaluation).map {
      case response if response.status == OK => {
        JsonParser.validateJsonParse[T](response.json) match {
          case Right(js: T) =>
            //            val j = ul.parseUnitLinksJson(js, period, `type`, history)
            //            println(j)
            // js as type and match to type class

            // we use js as unitLinks to extract id and type etc...
            Right(response)

          //            if (js.length == CAPPED_DISPLAY_NUMBER) {
          //              getUnitRecord
          //              /**
          //                * HERE WE INCLUDE THE CORE SEARCH REQUESTS
          //                */
          //              Right(???)
          //            } else {
          //              logger.debug(s"Found multiple records matching given id, $id. Returning multiple as list.")
          //              Right(response)
          //            }
          case Left(ex: JsResultException) => Left(ex)
        }
      } case response if response.status == NOT_FOUND => {
        // TODO ADD logger back and add proper period parser!! => requestEvaluation.isInstanceOf[PeriodRequest]
        logger.debug(s"Could find UnitLink for id ${requestEvaluation.id}.")
        Right(response)
      }
      case ex: Throwable => Left(ex)
    }
  }

  private def getUnitLinks(requestEvaluation: RequestEvaluation): Future[WSResponse] = {
    val unitLinkRequestUrl = createUri(props.SBR_CONTROL_API_URL, requestEvaluation)
    logger.debug(s"Sending request to $unitLinkRequestUrl to retrieve Unit Links")
    ws.singleGETRequest(unitLinkRequestUrl)
  }

  private def getUnitRecord(unitRequest: UnitRequest): Future[WSResponse] = {
    val path = findUrl(unitRequest.`type`)
    val dataRecordRequestUrl = createUri(path, unitRequest)
    logger.info(s"Sending request to $dataRecordRequestUrl to get records of all variables of unit.")
    ws.singleGETRequest(dataRecordRequestUrl)
  }

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
}
