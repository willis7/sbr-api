package uk.gov.ons.sbr.models

import scala.util.{ Failure, Success, Try }

import config.Properties

/**
 * DataSourceTypes
 * ----------------
 * Author: haqa
 * Date: 17 October 2017 - 09:25
 * Copyright (c) 2017  Office for National Statistics
 */

// TODO look for abstract Writer/ format
sealed trait DataSourceTypes { def path: String }

case object CRN extends DataSourceTypes {
  val path = "records"
  val referenceLabel = "CRN"
  override def toString = "CH"
}
case object VAT extends DataSourceTypes { val path = "records" }
case object PAYE extends DataSourceTypes { val path = "records" }
case object LEU extends DataSourceTypes { val path = "records" }
case object ENT extends DataSourceTypes { val path = "enterprises" }

// create DataSourceTypes.type from str
object DataSourceTypesUtil {
  def fromString(value: String): Option[DataSourceTypes] = {
    Vector(CRN, VAT, PAYE, LEU, ENT).find(_.toString.equalsIgnoreCase(value))
  }

  def ifExists(value: String): Boolean = {
    Vector(CRN, VAT, PAYE, LEU, ENT).exists(_.toString.equalsIgnoreCase(value))
  }

  // returns unit reference name for CH
  def converter(unit: String): String = unit match {
    case x if x == CRN.toString => CRN.referenceLabel
    case x => x
  }

  def getUrl(props: Properties)(unitType: DataSourceTypes): String =
    unitType match {
      case LEU => props.LEGAL_UNIT_DATA_API_URL
      case CRN => props.CH_ADMIN_DATA_API_URL
      case VAT => props.VAT_ADMIN_DATA_API_URL
      case PAYE => props.PAYE_ADMIN_DATA_API_URL
      case ENT => props.SBR_CONTROL_API_URL
    }

  def tryFindDataSource(`type`: String): Either[Throwable, DataSourceTypes] = {
    Try(fromString(`type`)) match {
      case Success(s) => Right(s.get)
      case Failure(ex) => Left(ex)
    }
  }
}
