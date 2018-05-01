package uk.gov.ons.sbr.models

import play.api.libs.json._

case class UnitDescriptor(
  idAndType: IdentifierAndType,
  period: Period,
  children: Seq[IdentifierAndType],
  unit: JsObject
)

object UnitDescriptor {
  private implicit val periodWrites = new Writes[Period] {
    override def writes(o: Period): JsValue =
      JsString(Period.asString(o))
  }

  private implicit val unitTypeWrites = new Writes[UnitType] {
    override def writes(o: UnitType): JsValue =
      JsString(acronymFor(o))
  }

  private def acronymFor(unitType: UnitType): String =
    unitType match {
      case UnitType.Enterprise => "ENT"
      case UnitType.LegalUnit => "LEU"
      case UnitType.LocalUnit => "LOU"
    }

  private implicit val identifierAndTypeWrites = Json.writes[IdentifierAndType]

  val writes = Json.writes[UnitDescriptor]

  def wrap(unitLinks: UnitLinks, unit: JsObject): UnitDescriptor =
    UnitDescriptor(
      unitLinks.idAndType,
      unitLinks.period,
      unitLinks.children,
      unit
    )
}