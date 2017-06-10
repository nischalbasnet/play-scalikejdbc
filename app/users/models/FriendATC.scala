package users.models

import org.joda.time.DateTime
import scalikejdbc.ParameterBinder
import scalikejdbc.interpolation.SQLSyntax

trait FriendATC
{
  self: Friend =>

  protected val _updateForm: FriendUpdateForm = FriendUpdateForm()

  def insertValuesMap: Map[SQLSyntax, ParameterBinder] =
  {
    val table = Friend.column
    val insertMap: Map[SQLSyntax, ParameterBinder] = Map(
      table.column("user_id") -> user_id,
      table.column("friend_user_id") -> friend_user_id,
      table.column("created") -> created,
      table.column("update") -> update
    )

    insertMap
  }

  def updateValuesMap: Map[SQLSyntax, ParameterBinder] = _updateForm.updateValuesMap

  def setUserId(inUserId: String) =
  {
    _updateForm.user_id = Some(inUserId)
    this
  }

  def setFriendUserId(inFriendUserId: String) =
  {
    _updateForm.friend_user_id = Some(inFriendUserId)
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
      user_id = _updateForm.user_id.getOrElse(user_id),
      friend_user_id = _updateForm.friend_user_id.getOrElse(friend_user_id),
      update = _updateForm.update.getOrElse(update),
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
  var user_id: Option[String] = None,
  var friend_user_id: Option[String] = None,
  var update: Option[DateTime] = None,
  var soft_deleted: Option[DateTime] = None
)
{
  def updateValuesMap: Map[SQLSyntax, ParameterBinder] =
  {
    val table = Friend.column
    var updateMap: Map[SQLSyntax, ParameterBinder] = Map.empty

    if (user_id.isDefined) updateMap = updateMap ++ Map(table.column("user_id") -> user_id.get)

    if (friend_user_id.isDefined) updateMap = updateMap ++ Map(table.column("friend_user_id") -> friend_user_id.get)

    if (update.isDefined) updateMap = updateMap ++ Map(table.column("update") -> update.get)

    if (soft_deleted.isDefined) updateMap = updateMap ++ Map(table.column("soft_deleted") -> soft_deleted.get)

    updateMap
  }
}

