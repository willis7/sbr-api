package uk.gov.ons.sbr.models

import java.time.YearMonth
import java.time.format.{ DateTimeFormatter, DateTimeParseException }

import scala.util.{ Failure, Success, Try }

import org.slf4j.{ Logger, LoggerFactory }
import com.google.inject.ImplementedBy

/**
 * RequestEvaluation
 * ----------------
 * Author: haqa
 * Date: 16 August 2017 - 09:25
 * Copyright (c) 2017  Office for National Statistics
 */
@ImplementedBy(classOf[ReferencePeriod])
sealed trait RequestEvaluation {
  def id: String
}
case class IdRequest(id: String) extends RequestEvaluation
case class ReferencePeriod(id: String, period: YearMonth) extends RequestEvaluation

case class InvalidKeyException(id: String, length: Int)
  extends Exception(s"Invalid key length exception $length with key $id")

object RequestEvaluationUtils {

  protected[this] val logger: Logger = LoggerFactory.getLogger(getClass.getName)
  // TODO - Inject config to get MINIMUM_KEY_LENGTH
  private val MINIMUM_KEY_LENGTH = 4

  val yearMonthFormat: String = "yyyyMM"

  def matchByParams(key: String, period: Option[String] = None): Either[Exception, RequestEvaluation] = {
    val id = validateKey(key).right.map(IdRequest)
    id.right.flatMap(
      i => if (period.isDefined) {
        validatePeriod(period.get).right.map(
          y => ReferencePeriod(i.id, y)
        )
      } else id
    )
  }

  def validateKey(key: String): Either[Exception, String] =
    if (key.length >= MINIMUM_KEY_LENGTH) Right(key)
    else Left(InvalidKeyException(key, MINIMUM_KEY_LENGTH))

  def validatePeriod(period: String): Either[Exception, YearMonth] = {
    val yearAndMonth = Try(YearMonth.parse(period, DateTimeFormatter.ofPattern(yearMonthFormat)))
    yearAndMonth match {
      case Success(ym) =>
        Right(ym)
      case Failure(ex: DateTimeParseException) =>
        logger.error("cannot parse date to YearMonth object", ex)
        Left(ex)
    }
  }

  def yearMonthAsString(ym: YearMonth): String = ym.format(DateTimeFormatter.ofPattern(yearMonthFormat))

}