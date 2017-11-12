package modelservices.address.models

import java.time.LocalDateTime

import com.nischal.base.{BaseModel, BaseModelCompanion}
import play.api.libs.json.{Json, Reads, Writes}
import scalikejdbc.{WrappedResultSet, autoConstruct}

case class Address(
  address_id: String,
  address_1: String,
  address_2: Option[String],
  city: Option[String],
  state_provience: Option[String],
  postal_code: Option[Int],
  created: LocalDateTime,
  updated: LocalDateTime,
  soft_deleted: Option[LocalDateTime],
  country: String
) extends BaseModel[Address] with AddressATC

/**
  * Addresses companion Object
  */
object Address extends AddressCompanionInfo
{
  val seqTypeCase = shapeless.TypeCase[Seq[Address]]

  implicit val reads: Reads[Address] = Json.format[Address]
  implicit val writes: Writes[Address] = Json.format[Address]
}

/**
  * Contains info the BaseModelCompanion
  */
trait AddressCompanionInfo extends BaseModelCompanion[Address]
{
  override val defaultTable: SQLSyntaxT = syntax("a")

  override val tableName = "addresses"

  override val primaryKey: String = "address_id"

  override val archivedField: Option[String] = Some("soft_deleted")

  override def fromSqlResult(
    rs: WrappedResultSet,
    rn: scalikejdbc.ResultName[Address] = defaultTable.resultName
  ): Address = autoConstruct(rs, rn)
}