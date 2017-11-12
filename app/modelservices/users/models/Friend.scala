package modelservices.users.models

import java.time.LocalDateTime

import com.nischal.base.{BaseModel, BaseModelCompanion}
import play.api.libs.json.{Json, Reads, Writes}
import scalikejdbc.{WrappedResultSet, autoConstruct}

case class Friend(
  friend_id: String,
  user_id: String,
  friend_user_id: String,
  created: LocalDateTime,
  updated: LocalDateTime,
  soft_deleted: Option[LocalDateTime]
) extends BaseModel[Friend] with FriendATC

/**
  * Friends companion Object
  */
object Friend extends FriendCompanionInfo
{
  //Important: don't remove this import
  import com.nischal.JsonReaderWriter._

  val seqTypeCase = shapeless.TypeCase[Seq[Friend]]

  implicit val reads: Reads[Friend] = Json.format[Friend]
  implicit val writes: Writes[Friend] = Json.format[Friend]
}

trait FriendCompanionInfo extends BaseModelCompanion[Friend]
{
  override val defaultTable: SQLSyntaxT = syntax("f")

  override val tableName = "friends"

  override val primaryKey: String = "friend_id"

  override val archivedField: Option[String] = Some("soft_deleted")

  override def fromSqlResult(
    rs: WrappedResultSet,
    rn: scalikejdbc.ResultName[Friend] = defaultTable.resultName
  ): Friend = autoConstruct(rs, rn)
}