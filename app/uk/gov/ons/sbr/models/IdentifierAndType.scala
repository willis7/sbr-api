package uk.gov.ons.sbr.models

import uk.gov.ons.sbr.models.IdentifierAndType.UnitIdentifier

case class IdentifierAndType(unitId: UnitIdentifier, unitType: UnitType) extends Product2[UnitIdentifier, UnitType] {
  override def _1: UnitIdentifier = unitId
  override def _2: UnitType = unitType
}

case object IdentifierAndType {
  type UnitIdentifier = String
}