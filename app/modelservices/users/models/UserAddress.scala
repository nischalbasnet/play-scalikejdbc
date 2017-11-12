package modelservices.users.models

import java.time.LocalDateTime

import com.nischal.base.{BaseModel, BaseModelCompanion}
import modelservices.address.dao.IAddressDAO
import modelservices.address.models.Address
import play.api.libs.json.{JsValue, Json, Reads, Writes}
import scalikejdbc.{AutoSession, DBSession, WrappedResultSet, autoConstruct}

case class UserAddress(
  user_address_id: String,
  tag_name: Option[String],
  description: Option[String],
  is_primary: Option[String],
  user_id: String,
  address_id: String,
  created: LocalDateTime,
  updated: LocalDateTime,
  soft_deleted: Option[LocalDateTime]
) extends BaseModel[UserAddress] with UserAddressATC with UserAddressRelations
{
  protected var _addressSetOnly: Address = _
}

/**
  * UserAddresses companion Object
  */
object UserAddress extends UserAddressCompanionInfo
{

  val seqTypeCase = shapeless.TypeCase[Seq[UserAddress]]

  implicit val reads: Reads[UserAddress] = Json.format[UserAddress]
  implicit val writes: Writes[UserAddress] = Json.format[UserAddress]

  val withAddress: Writes[UserAddress] = new Writes[UserAddress]
  {
    def writes(ua: UserAddress): JsValue =
    {
      Json.obj(
        "user_address_id" -> ua.user_address_id,
        "tag_name" -> ua.tag_name,
        "description" -> ua.description,
        "is_primary" -> ua.is_primary,
        "user_id" -> ua.user_id,
        "address_id" -> ua.address_id,
        "created" -> ua.created,
        "address" -> ua._addressSetOnly
      )
    }
  }
}

trait UserAddressCompanionInfo extends BaseModelCompanion[UserAddress]
{
  override val defaultTable: SQLSyntaxT = syntax("ua")

  override val tableName = "user_addresses"

  override val primaryKey: String = "user_address_id"

  override val archivedField: Option[String] = Some("soft_deleted")

  override def fromSqlResult(
    rs: WrappedResultSet,
    rn: scalikejdbc.ResultName[UserAddress] = defaultTable.resultName
  ): UserAddress = autoConstruct(rs, rn)
}

/**
  * UserAddress's relations
  */
trait UserAddressRelations
{
  self: UserAddress =>

  def address()(implicit addressDAO: IAddressDAO, session: DBSession = AutoSession): Address =
  {
    if (_addressSetOnly == null) {
      _addressSetOnly = addressDAO.getOrFail(address_id)
    }
    _addressSetOnly
  }

  def setAddress(address: Address): UserAddress =
  {
    _addressSetOnly = address
    this
  }
}