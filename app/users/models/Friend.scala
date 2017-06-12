package users.models

import javax.inject.Singleton

import com.nischal.base.{BaseModel, BaseModelCompanion}
import org.joda.time.DateTime
import play.api.libs.json.{Json, Reads, Writes}
import scalikejdbc.{WrappedResultSet, autoConstruct}

case class Friend(
  friend_id: String,
  user_id: String,
  friend_user_id: String,
  created: DateTime,
  update: DateTime,
  soft_deleted: Option[DateTime]
) extends BaseModel[Friend] with FriendATC

/**
  * Friends companion Object
  */
object Friend extends FriendCompanionInfo
{
  implicit val reads: Reads[Friend] = Json.format[Friend]
  implicit val writes: Writes[Friend] = Json.format[Friend]
}

trait FriendCompanionInfo extends BaseModelCompanion[Friend]
{
  override val defaultTable: SQLSyntaxT[Friend] = syntax("f")

  override val tableName = "friends"

  override val primaryKey: String = "friend_id"

  override val archivedField: Option[String] = Some("soft_deleted")

  override def fromSqlResult(rn: scalikejdbc.ResultName[Friend])(rs: WrappedResultSet): Friend = autoConstruct(rs, rn)
}