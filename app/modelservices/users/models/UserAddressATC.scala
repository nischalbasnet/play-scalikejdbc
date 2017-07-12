package modelservices.users.models

import org.joda.time.DateTime
import scalikejdbc.ParameterBinder
import scalikejdbc.interpolation.SQLSyntax

trait UserAddressATC
{
  self: UserAddress =>

  protected val _updateForm: UserAddressUpdateForm = UserAddressUpdateForm()

  def insertValuesMap: Map[SQLSyntax, ParameterBinder] =
  {
    val table = UserAddress.column
    val insertMap: Map[SQLSyntax, ParameterBinder] = Map(
      table.column("tag_name") -> tag_name,
      table.column("description") -> description,
      table.column("is_primary") -> is_primary,
      table.column("user_id") -> user_id,
      table.column("address_id") -> address_id,
      table.column("created") -> created,
      table.column("update") -> update
    )

    insertMap
  }

  def updateValuesMap: Map[SQLSyntax, ParameterBinder] = _updateForm.updateValuesMap

  def setTagName(inTagName: String) =
  {
    _updateForm.tag_name = Some(inTagName)
    this
  }

  def setDescription(inDescription: String) =
  {
    _updateForm.description = Some(inDescription)
    this
  }

  def setIsPrimary(inIsPrimary: String) =
  {
    _updateForm.is_primary = Some(inIsPrimary)
    this
  }

  def setUserId(inUserId: String) =
  {
    _updateForm.user_id = Some(inUserId)
    this
  }

  def setAddressId(inAddressId: String) =
  {
    _updateForm.address_id = Some(inAddressId)
    this
  }

  def setUpdate(inUpdate: DateTime) =
  {
    _updateForm.update = Some(inUpdate)
    this
  }

  def setSoftDeleted(inSoftDeleted: DateTime) =
  {
    _updateForm.soft_deleted = Some(inSoftDeleted)
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
      update = _updateForm.update.getOrElse(update),
      soft_deleted = _updateForm.soft_deleted match {
        case Some(s: DateTime) => Some(s)
        case _ => soft_deleted
      }
    )
  }

}

/**
  * UserAddresses companion Object
  */
case class UserAddressUpdateForm(
  var tag_name: Option[String] = None,
  var description: Option[String] = None,
  var is_primary: Option[String] = None,
  var user_id: Option[String] = None,
  var address_id: Option[String] = None,
  var update: Option[DateTime] = None,
  var soft_deleted: Option[DateTime] = None
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

    if (update.isDefined) updateMap = updateMap ++ Map(table.column("update") -> update.get)

    if (soft_deleted.isDefined) updateMap = updateMap ++ Map(table.column("soft_deleted") -> soft_deleted.get)

    updateMap
  }
}

