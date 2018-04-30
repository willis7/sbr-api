package uk.gov.ons.sbr.models

import play.api.libs.json.JsValue

case class UnitDescriptor(id: String, children: Map[String, String], unitType: String, period: String, vars: JsValue)
