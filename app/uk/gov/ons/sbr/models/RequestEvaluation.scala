package uk.gov.ons.sbr.models

import java.time.{ DateTimeException, YearMonth }

import com.google.inject.ImplementedBy
import play.api.libs.json.JsError

/**
  * Created by haqa on 09/08/2017.
  */
@ImplementedBy(classOf[ReferencePeriod])
sealed trait RequestEvaluation

case class IdRequest(id: String) extends RequestEvaluation
case class ReferencePeriod(id: String, period: YearMonth) extends RequestEvaluation
case class CategoryRequest(id: String, category: String) extends RequestEvaluation
case class InvalidReferencePeriod(id: String, exception: DateTimeException) extends RequestEvaluation
case class InvalidKey(id: String) extends RequestEvaluation