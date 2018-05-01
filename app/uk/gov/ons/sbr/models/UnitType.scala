package uk.gov.ons.sbr.models

sealed trait UnitType

object UnitType {
  case object Enterprise extends UnitType
  case object LegalUnit extends UnitType
  case object LocalUnit extends UnitType
}