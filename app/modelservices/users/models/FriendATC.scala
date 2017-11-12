package modelservices.users.models

import org.joda.time.DateTime
import scalikejdbc.{ParameterBinder, autoNamedValues}
import scalikejdbc.interpolation.SQLSyntax

trait FriendATC
{
  self: Friend =>

  protected var _updateForm: FriendUpdateForm = FriendUpdateForm()

  def getInsertValuesMap: Map[SQLSyntax, ParameterBinder] =
  {
    val insertMap = autoNamedValues[Friend](
      this,
      Friend.column,
      "friend_id",
      "created",
      "updated",
      "soft_deleted"
    )
    //    val insertMap: Map[SQLSyntax, ParameterBinder] = Map(
    //      table.column("user_id") -> user_id,
    //      table.column("friend_user_id") -> friend_user_id,
    //      table.column("created") -> created,
    //      table.column("update") -> update
    //    )

    insertMap
  }

  def getUpdateValuesMap: Map[SQLSyntax, ParameterBinder] = _updateForm.updateValuesMap

  def setUserId(inUserId: String) =
  {
    _updateForm = _updateForm.copy(user_id = Some(inUserId))
    this
  }

  def setFriendUserId(inFriendUserId: String) =
  {
    _updateForm = _updateForm.copy(friend_user_id = Some(inFriendUserId))
    this
  }

  def setUpdated(inUpdated: DateTime) =
  {
    _updateForm = _updateForm.copy(updated = Some(inUpdated))
    this
  }

  def setSoftDeleted(inSoftDeleted: DateTime) =
  {
    _updateForm = _updateForm.copy(soft_deleted = Some(inSoftDeleted))
    this
  }

  def syncOriginal() =
  {
    this.copy(
      user_id = _updateForm.user_id.getOrElse(user_id),
      friend_user_id = _updateForm.friend_user_id.getOrElse(friend_user_id),
      updated = _updateForm.updated.getOrElse(updated),
      soft_deleted = _updateForm.soft_deleted match {
        case Some(s: DateTime) => Some(s)
        case _ => soft_deleted
      }
    )
  }

}

/**
  * Friends companion Object
  */
case class FriendUpdateForm(
  user_id: Option[String] = None,
  friend_user_id: Option[String] = None,
  updated: Option[DateTime] = None,
  soft_deleted: Option[DateTime] = None
)
{
  def updateValuesMap: Map[SQLSyntax, ParameterBinder] =
  {
    val table = Friend.column
    var updateMap: Map[SQLSyntax, ParameterBinder] = Map.empty

    if (user_id.isDefined) updateMap = updateMap ++ Map(table.column("user_id") -> user_id.get)

    if (friend_user_id.isDefined) updateMap = updateMap ++ Map(table.column("friend_user_id") -> friend_user_id.get)

    if (updated.isDefined) updateMap = updateMap ++ Map(table.column("update") -> updated.get)

    if (soft_deleted.isDefined) updateMap = updateMap ++ Map(table.column("soft_deleted") -> soft_deleted.get)

    updateMap
  }
}

