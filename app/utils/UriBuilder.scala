package utils

import uk.gov.ons.sbr.RequestEvaluationUtils.yearMonthAsString
import uk.gov.ons.sbr._
import uk.gov.ons.sbr.models._

/**
 * UriBuilder
 * ----------------
 * Author: haqa
 * Date: 16 August 2017 - 09:25
 * Copyright (c) 2017  Office for National Statistics
 */
object UriBuilder {

  private val PERIOD_PATH = "periods"
  private val TYPE_PATH = "types"
  private val UNIT_PATH = "units"
  private val HISTORY_PATH = "history"
  private val HISTORY_MAX_ARG = "max"

  // @TODO - Remove group parameter
  def createUri(baseUrl: String, requestEvaluation: RequestEvaluation, group: String = ""): String = {
    val unitTypePath = DataSourceTypesUtil.fromString(group).getOrElse(None) match {
      case x: DataSourceTypes => x.path
      case _ => UNIT_PATH
    }
    requestEvaluation match {
      case (u: UnitRequest) =>
        s"$baseUrl/$PERIOD_PATH/${yearMonthAsString(u.period)}/$TYPE_PATH/${u.`type`.toString}/$unitTypePath/${u.id}"
      case (p: PeriodRequest) => if (List(VAT.toString, CRN.toString, PAYE.toString, LEU.toString) contains group) {
        s"$baseUrl/$unitTypePath/${p.id}/$PERIOD_PATH/${yearMonthAsString(p.period)}"
      } else {
        s"$baseUrl/$PERIOD_PATH/${yearMonthAsString(p.period)}/$unitTypePath/${p.id}"
      }
      case (h: IdHistoryRequest) => s"$baseUrl/$unitTypePath/${h.id}/$HISTORY_PATH?$HISTORY_MAX_ARG=${h.history}"
      case (i: IdRequest) => s"$baseUrl/$unitTypePath/${i.id}"
    }
  }

}
