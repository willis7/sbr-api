package uk.gov.ons.sbr.models

import java.time.YearMonth

import io.swagger.annotations.ApiModelProperty

case class StatisticalBusinessRegisterRecord[T](
  @ApiModelProperty(example = "", dataType = "String") id: String,
  @ApiModelProperty(value = "A map of parents of returned id [Type, Value]", example = "",
    dataType = "Map[String,String]") parents: Option[Map[String, String]],
  @ApiModelProperty(value = "A string of all related children", example = "") children: Option[Map[String, String]],
  @ApiModelProperty(value = "Type of Unit returned", example = "") unitType: String,
  period: YearMonth,
  variables: Map[String, String],
  childrenJson: Option[T] = None
)

object StatisticalBusinessRegisterRecord {

  type EnterpriseChildrenJson = List[(String, String, Option[List[(String, String)]])]
  type ChildrenJson = List[(String, String)]

  //  implicit val SBRRecordEnterpriseFormat: OFormat[StatisticalBusinessRegisterRecord[EnterpriseChildrenJson]] =
  //    Json.format[StatisticalBusinessRegisterRecord[EnterpriseChildrenJson]]
  //
  //  implicit val SBRRecordFormat: OFormat[StatisticalBusinessRegisterRecord[ChildrenJson]] =
  //    Json.format[StatisticalBusinessRegisterRecord[ChildrenJson]]
  //
  //  // TODO - MERGE both apply functions!
  //  def apply(unit: JsValue, link: JsValue, unitType: String, date: JsValue, vars: JsValue): StatisticalBusinessRegisterRecord[EnterpriseChildrenJson] = {
  //    val id = (link \ "id").as[String]
  //    val parents = (link \ "parents").as[Option[Map[String, String]]]
  //    val children = (link \ "children").as[Option[Map[String, String]]]
  //    val childrenJson = (unit \ "childrenJson").as[Option[EnterpriseChildrenJson]]
  //    // TODO - Use validateYearMonth + *** WARN *** JsNull will break period *** WARN ***
  //    val period = YearMonth.parse(date.as[String], DateTimeFormatter.ofPattern(RequestEvaluationUtils.yearMonthFormat))
  //    // TODO - *** WARN *** JsNull will break vars*** WARN ***
  //    val variables = vars.as[Map[String, String]]
  //    StatisticalBusinessRegisterRecord(id, parents, children, unitType, period, variables, childrenJson)
  //  }
  //
  //  def apply(link: JsValue, unitType: String, date: JsValue, vars: JsValue): StatisticalBusinessRegisterRecord[ChildrenJson] = {
  //    val id = (link \ "id").as[String]
  //    val parents = (link \ "parents").as[Option[Map[String, String]]]
  //    val children = (link \ "children").as[Option[Map[String, String]]]
  //    // TODO - Use validateYearMonth + *** WARN *** JsNull will break period *** WARN ***
  //    val period = YearMonth.parse(date.as[String], DateTimeFormatter.ofPattern(RequestEvaluationUtils.yearMonthFormat))
  //    // TODO - *** WARN *** JsNull will break vars*** WARN ***
  //    val variables = vars.as[Map[String, String]]
  //    StatisticalBusinessRegisterRecord(id, parents, children, unitType, period, variables)
  //  }

}