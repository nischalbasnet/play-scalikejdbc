package modelservices.users.models

import java.time.LocalDateTime

import scalikejdbc.interpolation.SQLSyntax
import scalikejdbc.{ParameterBinder, autoNamedValues}

trait UserAddressATC
{
  self: UserAddress =>

  protected var _updateForm: UserAddressUpdateForm = UserAddressUpdateForm()

  def getInsertValuesMap: Map[SQLSyntax, ParameterBinder] =
  {
    val insertMap = autoNamedValues[UserAddress](
      this,
      UserAddress.column,
      "user_address_id",
      "created",
      "updated",
      "soft_deleted"
    )
//    val insertMap: Map[SQLSyntax, ParameterBinder] = Map(
//      table.column("tag_name") -> tag_name,
//      table.column("description") -> description,
//      table.column("is_primary") -> is_primary,
//      table.column("user_id") -> user_id,
//      table.column("address_id") -> address_id,
//      table.column("created") -> created,
//      table.column("update") -> update
//    )

    insertMap
  }

  def getUpdateValuesMap: Map[SQLSyntax, ParameterBinder] = _updateForm.updateValuesMap

  def setTagName(inTagName: String) =
  {
    _updateForm = _updateForm.copy(tag_name = Some(inTagName))
    this
  }

  def setDescription(inDescription: String) =
  {
    _updateForm = _updateForm.copy(description = Some(inDescription))
    this
  }

  def setIsPrimary(inIsPrimary: String) =
  {
    _updateForm = _updateForm.copy(is_primary = Some(inIsPrimary))
    this
  }

  def setUserId(inUserId: String) =
  {
    _updateForm = _updateForm.copy(user_id = Some(inUserId))
    this
  }

  def setAddressId(inAddressId: String) =
  {
    _updateForm = _updateForm.copy(address_id = Some(inAddressId))
    this
  }

  def setUpdated(inUpdated: LocalDateTime) =
  {
    _updateForm = _updateForm.copy(updated = Some(inUpdated))
    this
  }

  def setSoftDeleted(inSoftDeleted: LocalDateTime) =
  {
    _updateForm = _updateForm.copy(soft_deleted = Some(inSoftDeleted))
    this
  }

  def syncOriginal() =
  {
    this.copy(
      tag_name = _updateForm.tag_name match {
        case Some(s: String) => Some(s)
        case _ => tag_name
      },
      description = _updateForm.description match {
        case Some(s: String) => Some(s)
        case _ => description
      },
      is_primary = _updateForm.is_primary match {
        case Some(s: String) => Some(s)
        case _ => is_primary
      },
      user_id = _updateForm.user_id.getOrElse(user_id),
      address_id = _updateForm.address_id.getOrElse(address_id),
      updated = _updateForm.updated.getOrElse(updated),
      soft_deleted = _updateForm.soft_deleted match {
        case Some(s: LocalDateTime) => Some(s)
        case _ => soft_deleted
      }
    )
  }

}

/**
  * UserAddresses companion Object
  */
case class UserAddressUpdateForm(
  tag_name: Option[String] = None,
  description: Option[String] = None,
  is_primary: Option[String] = None,
  user_id: Option[String] = None,
  address_id: Option[String] = None,
  updated: Option[LocalDateTime] = None,
  soft_deleted: Option[LocalDateTime] = None
)
{
  def updateValuesMap: Map[SQLSyntax, ParameterBinder] =
  {
    val table = UserAddress.column
    var updateMap: Map[SQLSyntax, ParameterBinder] = Map.empty

    if (tag_name.isDefined) updateMap = updateMap ++ Map(table.column("tag_name") -> tag_name.get)

    if (description.isDefined) updateMap = updateMap ++ Map(table.column("description") -> description.get)

    if (is_primary.isDefined) updateMap = updateMap ++ Map(table.column("is_primary") -> is_primary.get)

    if (user_id.isDefined) updateMap = updateMap ++ Map(table.column("user_id") -> user_id.get)

    if (address_id.isDefined) updateMap = updateMap ++ Map(table.column("address_id") -> address_id.get)

    if (updated.isDefined) updateMap = updateMap ++ Map(table.column("update") -> updated.get)

    if (soft_deleted.isDefined) updateMap = updateMap ++ Map(table.column("soft_deleted") -> soft_deleted.get)

    updateMap
  }
}

