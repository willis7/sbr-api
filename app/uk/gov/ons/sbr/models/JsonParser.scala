package uk.gov.ons.sbr.models

import play.api.libs.json.JsValue

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

}
