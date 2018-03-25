package utils

import java.time.YearMonth

import uk.gov.ons.sbr._
import uk.gov.ons.sbr.models._
import uk.gov.ons.sbr.RequestEvaluationUtils.yearMonthAsString

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

  /**
   *
   * @param baseUrl - url of the api.
   * @param units - id
   * @param periods - Optional
   * @param types - Optional
   * @param group - used to trigger Unit Type Search. If passed then assumed group is a string Unit Type to get vars.
   * @param history - Optional, limits the result size when period isn't given.
   * @return {String} url
   */
  @deprecated("Migrated to createUri with RequestEvaluation param", "fix/refactor-code - 25 March 2018")
  def createUriOLD(baseUrl: String, units: String, periods: Option[YearMonth] = None, types: Option[DataSourceTypes] = None,
    group: String = "", history: Option[Int] = None): String = {
    val unitTypePath = DataSourceTypesUtil.fromString(group).getOrElse(None) match {
      case x: DataSourceTypes => x.path
      case _ => UNIT_PATH
    }
    (periods, types, units, history) match {
      case (Some(p), Some(t), u, None) => s"$baseUrl/$PERIOD_PATH/${yearMonthAsString(p)}/$TYPE_PATH/${t.toString}/$unitTypePath/$u"
      case (Some(p), None, u, None) => if (List(VAT.toString, CRN.toString, PAYE.toString, LEU.toString) contains group) {
        s"$baseUrl/$unitTypePath/$u/$PERIOD_PATH/${yearMonthAsString(p)}"
      } else {
        s"$baseUrl/$PERIOD_PATH/${yearMonthAsString(p)}/$unitTypePath/$u"
      }
      case (None, None, u, Some(h)) => s"$baseUrl/$unitTypePath/$u/$HISTORY_PATH?$HISTORY_MAX_ARG=$h"
      case (None, Some(t), u, None) => s"$baseUrl/$TYPE_PATH/${t.toString}/$unitTypePath/$u"
      case _ => s"$baseUrl/$unitTypePath/$units"
    }
  }

  // @TODO - Remove group parameter
  def createUri(baseUrl: String, requestEvaluation: RequestEvaluation, group: String = ""): String = {
    val unitTypePath = DataSourceTypesUtil.fromString(group).getOrElse(None) match {
      case x: DataSourceTypes => x.path
      case _ => UNIT_PATH
    }
    requestEvaluation match {
      case (u: UnitRequest) => s"$baseUrl/$PERIOD_PATH/${yearMonthAsString(u.period)}/$TYPE_PATH/${u.`type`.toString}/$unitTypePath/${u.id}"
      case (p: PeriodRequest) => if (List(VAT.toString, CRN.toString, PAYE.toString, LEU.toString) contains group) {
        s"$baseUrl/$unitTypePath/${p.id}/$PERIOD_PATH/${yearMonthAsString(p.period)}"
      } else {
        s"$baseUrl/$PERIOD_PATH/${yearMonthAsString(p.period)}/$unitTypePath/${p.id}"
      }
      case (h: IdHistoryRequest) => s"$baseUrl/$unitTypePath/${h.id}/$HISTORY_PATH?$HISTORY_MAX_ARG=${h.history}"
      //      case (None, Some(t), u, None) => s"$baseUrl/$TYPE_PATH/${t.toString}/$unitTypePath/$u"
      case (i: IdRequest) => s"$baseUrl/$unitTypePath/${i.id}"
    }
  }

}
