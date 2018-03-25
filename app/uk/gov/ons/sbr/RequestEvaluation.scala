package uk.gov.ons.sbr

import java.time.YearMonth
import java.time.format.{ DateTimeFormatter, DateTimeParseException }

import scala.util.{ Failure, Success, Try }

import org.slf4j.{ Logger, LoggerFactory }
import com.google.inject.ImplementedBy

import uk.gov.ons.sbr.models.DataSourceTypes

/**
 * RequestEvaluation
 * ----------------
 * Author: haqa
 * Date: 16 August 2017 - 09:25
 * Copyright (c) 2017  Office for National Statistics
 */
@ImplementedBy(classOf[PeriodRequest])
sealed trait RequestEvaluation {
  def id: String
}
case class IdRequest(id: String) extends RequestEvaluation
case class PeriodRequest(id: String, period: YearMonth) extends RequestEvaluation
case class IdHistoryRequest(id: String, history: Int) extends RequestEvaluation
case class UnitRequest(id: String, period: YearMonth, `type`: DataSourceTypes) extends RequestEvaluation

case class InvalidKeyException(id: String, length: Int)
  extends Exception(s"Invalid key length exception $length with key $id")

object RequestEvaluationUtils {

  protected[this] val logger: Logger = LoggerFactory.getLogger(getClass.getName)
  // TODO - Inject config to get MINIMUM_KEY_LENGTH
  val MINIMUM_KEY_LENGTH = 4

  val yearMonthFormat: String = "yyyyMM"

  // WRAPPER
  def getUnitMatchRequest(id: String, period: Option[String] = None, `type`: Option[DataSourceTypes] = None): Either[Exception, RequestEvaluation] =
    //     `type`.fold(matchByParams(id, period))(
    //       d => matchByParams(id, period).right.map(
    //         x => UnitRequest(x.id, x.asInstanceOf[PeriodRequest].period, d)
    //       )
    //     )
    `type`.fold(matchByParams(id, period))(
      d => matchByParams(id, period).right.map {
        case (x: PeriodRequest) => UnitRequest(x.id, x.period, d)
      }
    )

  def matchByParams(key: String, period: Option[String] = None): Either[Exception, RequestEvaluation] = {
    val id = validateKey(key).right.map(IdRequest)
    id.right.flatMap(
      i => if (period.isDefined) {
        validatePeriod(period.get).right.map(
          y => PeriodRequest(i.id, y)
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