package uk.gov.ons.sbr

import java.time.YearMonth

import play.api.libs.json.{ JsResultException, JsValue, Reads }

import uk.gov.ons.sbr.models.{ DataSourceTypes, DataSourceTypesUtil, ENT }

import utils.Utilities.orElseNull

object JsonParser {

  def parse(record: (JsValue, JsValue), `type`: String): JsValue = {
    record match {
      case (link, unit) =>

        // @ TODO PATCH - fix and remove patch *** WARN ***
        // BI does not have period, so use an empty string
        val period = if (`type` == ENT.toString) {
          (unit \ "period").getOrNull
        } else {
          (unit.as[JsValue] \ "period").getOrNull
        }

        // @ TODO PATCH - fix and remove patch when BI and ENTERPRISE apis are fixed *** WARN ***
        // For BI, there is no "vars", just use the whole record
        //        val vars = if (`type` == ENT.toString || `type` == LEU.toString) {
        val vars = if (`type` == ENT.toString) {
          (unit \ "vars").getOrElse(unit)
        } else {
          (unit.as[JsValue] \ "variables").getOrNull
        }

        // TODO - remove "" and use OPTION
        val unitType = DataSourceTypesUtil.fromString(`type`).getOrElse("").toString

        // Only return childrenJson with an Enterprise
        //Json.obj(
        unitType match {
          case "ENT" =>
            //          case ENT.toString =>
            //            Json.toJson(StatisticalBusinessRegisterRecord(link, unit, unitType, period, vars))
            ???
          case _ =>
            //            Json.toJson(StatisticalBusinessRegisterRecord(link, unitType, period, vars))
            ???
        } //).fields.filterNot { case (_, v) => v == JsNull }
    }
  }

  def validateJsonParse[T](json: JsValue)(implicit reads: Reads[T]): Either[Exception, T] = {
    json.validate[T](reads).fold(
      invalid = err => Left(JsResultException(err)),
      valid = v => Right(v)
    )
  }

  sealed trait UnitLinksType[T] {
    def parseUnitLinksJson(json: T, period: Option[YearMonth] = None, unitType: Option[DataSourceTypes] = None,
      history: Option[Int] = None): T
  }

  object UnitLinksType {
    object UnitLinksListType extends UnitLinksType[Seq[JsValue]] {
      override def parseUnitLinksJson(json: Seq[JsValue], period: Option[YearMonth] = None,
        unitType: Option[DataSourceTypes] = None, history: Option[Int] = None): Seq[JsValue] = json
      /**
       * case s: UnitLinksListType =>
       * // if one UnitLinks found -> get unit
       * if (u.length == CAPPED_DISPLAY_NUMBER) {
       * val id = (u.head \ "id").as[String]
       * logger.debug(s"Found a single response with $id")
       * val mapOfRecordKeys = Map((u.head \ "unitType").as[String] -> id)
       * val respRecords = parsedRequest(mapOfRecordKeys, periodParam, history)
       * val json: Seq[JsValue] = (u zip respRecords).map(x => toJson(x, (u.head \ "unitType").as[String]))
       * Ok(Json.toJson(json)).as(JSON)
       * } else {
       * logger.debug(s"Found multiple records matching given id, $key. Returning multiple as list.")
       * // return UnitLinks if multiple
       * PartialContent(unitResp.toString).as(JSON)
       * }
       */
    }
    object StatisticalUnitLinkType extends UnitLinksType[JsValue] {
      override def parseUnitLinksJson(json: JsValue, period: Option[YearMonth] = None,
        unitType: Option[DataSourceTypes] = None, history: Option[Int] = None): JsValue = json
      //      {
      //        val id = (resp \ "id").as[String]
      //        val `type` = unitType.toString
      //        val respRecords = searchService.getUnitRecord(id, period, unitType)
      //        val json = (Seq(resp) zip respRecords).map(x => parse(x, `type`)).head
      //      }
      /**
       * case s: StatisticalUnitLinkType =>
       * val mapOfRecordKeys = Map(sourceType.toString -> (s \ "id").as[String])
       * val respRecords = parsedRequest(mapOfRecordKeys, periodParam)
       * val json = (Seq(s) zip respRecords).map(x => toJson(x, sourceType.toString)).head
       * Ok(json).as(JSON)
       */
    }
  }
}

//object Test {
//  def search[T](json: T)(implicit i: UnitLinksType[T]) = {
//    i.parseUnitLinksJson(json)
//  }
//
//  def search[T](json: T)(ul: UnitLinksType[T]) = {
//    ul.parseUnitLinksJson(json)
//  }
//
//}
