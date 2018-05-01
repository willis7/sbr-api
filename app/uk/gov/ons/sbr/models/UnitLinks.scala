package uk.gov.ons.sbr.models

case class UnitLinks(idAndType: IdentifierAndType, period: Period, children: Seq[IdentifierAndType])
