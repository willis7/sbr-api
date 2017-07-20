package models.units

import io.swagger.annotations.ApiModelProperty
import models.units.attributes.Address
import utils.Mapping
import utils.Utilities._

/**
 * Created by Ameen on 15/07/2017.
 */
final case class Enterprise(
  @ApiModelProperty(value = "", example = "", required = false, hidden = false) name: String,
  @ApiModelProperty(value = "", example = "", dataType = "java.lang.Long") id: Long,
  @ApiModelProperty(value = "", example = "", dataType = "java.lang.String") legalUnits: Seq[Option[Long]],
  @ApiModelProperty(dataType = "Address") address: Address,
  postcode: String,
  @ApiModelProperty(value = "", example = "", dataType = "java.lang.Integer") legalStatus: Option[Int],
  @ApiModelProperty(value = "", example = "", dataType = "java.lang.Integer") sic: Option[Int],
  @ApiModelProperty(value = "", example = "", dataType = "java.lang.Integer") employees: Option[Int],
  @ApiModelProperty(value = "", example = "", dataType = "java.lang.Integer") workingGroup: Option[Int],
  @ApiModelProperty(value = "", example = "", dataType = "java.lang.Integer") employment: Option[Int],
  @ApiModelProperty(value = "", example = "", dataType = "java.lang.Long") turnover: Option[Long],
  source: String = "Enterprise"
) extends Searchkeys[Long]

object EnterpriseObj extends Mapping[Enterprise, Array[String]] {

  def toMap(v: Enterprise): Map[String, Any] = Map(
    "name" -> v.name,
    "id" -> v.id,
    "legalUnits" -> v.legalUnits.map(x => getElement(x)),
    "address" -> v.address,
    "postcode" -> v.postcode,
    "legalStatus" -> v.legalStatus,
    "sic" -> v.sic,
    "employees" -> v.employees,
    "workingGrouping" -> v.workingGroup,
    "employment" -> v.employment,
    "turnover" -> v.turnover
  )

  def fromMap(values: Array[String]): Enterprise =
    Enterprise(values(0), values(1).toLong, filterChildren(values), Address(values(6), values(7), values(8), values(9),
      values(10)), values(11), Option(values(12).toInt), Option(values(13).toInt), Option(values(14).toInt),
      Option(values(15).toInt), Option(values(16).toInt), Option(values(17).toLong))

  def filterChildren(values: Array[String]): Seq[Option[Long]] = Seq(Option(values(2).toLong), Option(values(3).toLong),
    Option(values(4).toLong), Option(values(5).toLong)).flatten.map(x => Option(x))

}
