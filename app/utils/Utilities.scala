package utils

import java.io.File
import java.time.YearMonth
import java.time.format.{ DateTimeFormatter, DateTimeParseException }

import scala.util.{ Failure, Success, Try }

import play.api.libs.json.{ JsLookupResult, JsNull, JsObject, JsValue, Json }
import org.slf4j.LoggerFactory

/**
 * UriBuilder
 * ----------------
 * Author: haqa & coolit
 * Date: 16 August 2017 - 09:25
 * Copyright (c) 2017  Office for National Statistics
 */
object Utilities {

  private[this] val logger = LoggerFactory.getLogger(getClass)
  val yearMonthFormat = "yyyyMM"

  private def currentDirectory = new File(".").getCanonicalPath

  @deprecated("Migrated to errAsJson with 2 params", "feature/new-admin-routes - 31 January 2018")
  def errAsJson(status: Int, code: String, msg: String, cause: String): JsObject = {
    Json.obj(
      "status" -> status,
      "code" -> code,
      "route_with_cause" -> cause,
      "message_en" -> msg
    )
  }

  def errAsJson(msg: String, cause: String = "Not traced"): JsObject = {
    Json.obj(
      "route_with_cause" -> cause,
      "message_en" -> msg
    )
  }

  // TODO - REMOVE
  def getElement(value: AnyRef) = {
    val res = value match {
      case None => ""
      case Some(i: Int) => i
      case Some(l: Long) => l
      case Some(z) => s"""${z.toString}"""
    }
    res
  }

  def unquote(s: String) = s.replace("\"", "")

  implicit class orElseNull(val j: JsLookupResult) {
    def getOrNull: JsValue = j.getOrElse(JsNull)
  }

  @throws(classOf[DateTimeParseException])
  def validateYearMonth(key: String, raw: String) = {
    val yearAndMonth = Try(YearMonth.parse(raw, DateTimeFormatter.ofPattern(yearAndMonth)))
    (yearAndMonth: @unchecked) match {
      case Success(s) => s
      case Failure(ex: DateTimeParseException) =>
        logger.error("cannot parse date to YearMonth object", ex)
        throw ex
    }
  }

}