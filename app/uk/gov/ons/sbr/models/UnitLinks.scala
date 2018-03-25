package uk.gov.ons.sbr.models

import java.time.YearMonth

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

import uk.gov.ons.sbr.RequestEvaluationUtils
import uk.gov.ons.sbr.models.UnitLinks.Attributes // Combinator syntax

/**
 * UnitLinks
 * ----------------
 * Author: haqa
 * Date: 15 March 2018 - 13:14
 * Copyright (c) 2017  Office for National Statistics
 */

case class UnitLinks(
  id: String,
  // NOTE - currently there is no period
  // period: YearMonth,
  unitType: DataSourceTypes,
  attributes: Attributes
)

object UnitLinks {

  type Attributes = JsObject

  // TODO - Create a YearMonth parser with validation
  implicit val yearMonthReads: Reads[YearMonth] = Reads[YearMonth](js =>
    js.validate[String].map[YearMonth](y =>
      RequestEvaluationUtils.validatePeriod(y).right.get))

  //TODO - ADD validation with either
  implicit val unitTypeReads: Reads[DataSourceTypes] = Reads[DataSourceTypes](js =>
    js.validate[String].map[DataSourceTypes](u =>
      DataSourceTypesUtil.tryFindDataSource(u).right.get))

  // TODO - fix and remove "id" - "period" - "unitType"
  implicit val attributesReads: Reads[Attributes] = Reads[Attributes](js =>
    js.validate[JsObject].map[Attributes](j => j - "id" - "period" - "unitType"))

  //  implicit val unitLinksFormat: Reads[UnitLinks] = Json.reads[UnitLinks]

  implicit val unitLinksReads: Reads[UnitLinks] = (
    (JsPath \ "id").read[String](minLength[String](RequestEvaluationUtils.minimumKeyLength)) and
    //      (JsPath \ "period").read[YearMonth] and
    (JsPath \ "unitType").read[DataSourceTypes] and
    JsPath.read[Attributes]
  )(UnitLinks.apply _)

  //  implicit val listUnitLinksFormat: OFormat[Seq[UnitLinks]] = Json.format[Seq[UnitLinks]]

  //  implicit val unitLinksRead: Reads[UnitLinks] = Json.reads[UnitLinks]
  //  implicit val listUnitLinksReads: Reads[Seq[UnitLinks]] = Json.reads[Seq[UnitLinks]]
  //  implicit val listUnitLinksWrites: OWrites[Seq[UnitLinks]] = Json.writes[Seq[UnitLinks]]

  //  implicit val unitLinksWriter: Writes[UnitLinks] = new Writes[UnitLinks] {
  //    override def writes(u: UnitLinks): JsValue =
  //      Json.obj(
  //        "id" -> u.id,
  //        "period" -> u.period.toString,
  //        "unitType" -> u.unitType.toString,
  //        "structure" -> u.structure
  //      )
  //  }

  //  def apply(json: JsValue): Either[Throwable, UnitLinks] = {
  //    val id = (json \ "id").as[String]
  //    val unitType = DataSourceTypesUtil.tryFindDataSource((json \ "unitType").as[String])
  //    val period = RequestEvaluationUtils.validatePeriod((json \ "period").as[String])
  //    val structure = getStructure(json)
  //
  //    period.right.flatMap(
  //      p => unitType.right.map(u =>
  //        new UnitLinks(id, p, u, structure))
  //    )
  //  }

  //  def getStructure(json: JsValue): JsObject = json.as[JsObject] - "id" - "period" - "unitType"
}