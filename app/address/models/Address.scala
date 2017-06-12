package address.models

import com.nischal.base.{BaseModel, BaseModelCompanion}
import org.joda.time.DateTime
import play.api.libs.json.{Json, Reads, Writes}
import scalikejdbc.{WrappedResultSet, autoConstruct}

case class Address(
  address_id: String,
  address_1: String,
  address_2: Option[String],
  city: Option[String],
  state_provience: Option[String],
  postal_code: Option[Int],
  created: DateTime,
  updated: DateTime,
  soft_deleted: Option[DateTime],
  country: String
) extends BaseModel[Address] with AddressATC

/**
  * Addresses companion Object
  */
object Address extends AddressCompanionInfo
{

  import com.nischal.JsonReaderWriter._

  implicit val reads: Reads[Address] = Json.format[Address]
  implicit val writes: Writes[Address] = Json.format[Address]
}

/**
  * Contains info the BaseModelCompanion
  */
trait AddressCompanionInfo extends BaseModelCompanion[Address]
{
  override val defaultTable: SQLSyntaxT[Address] = syntax("a")

  override val tableName = "addresses"

  override val primaryKey: String = "address_id"

  override val archivedField: Option[String] = Some("soft_deleted")

  override def fromSqlResult(rn: scalikejdbc.ResultName[Address])(rs: WrappedResultSet): Address = autoConstruct(rs, rn)
}