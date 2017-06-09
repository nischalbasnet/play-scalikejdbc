package users.models

import com.nischal.base.BaseModelCompanion
import org.joda.time.DateTime
import play.api.libs.json.{Json, Reads, Writes}
import scalikejdbc.{WrappedResultSet, autoConstruct}

case class Gender(
  gender_id: String,
  gender_name: String,
  created: DateTime,
  updated: DateTime,
  soft_deleted: Option[DateTime],
  ord: Int
)

/**
  * Gender companion Object
  */
object Gender extends BaseModelCompanion[Gender]
{
  implicit val reads: Reads[Gender] = Json.format[Gender]
  implicit val writes: Writes[Gender] = Json.format[Gender]

  override val defaultTable: SQLSyntaxT[Gender] = this.syntax("g")

  override val primaryKey: String = "gender_id"

  override val tableName: String = "genders"

  override val archivedField: Option[String] = Some("soft_deleted")

  override def fromSqlResult(rn: scalikejdbc.ResultName[Gender])(rs: WrappedResultSet): Gender =
  {
    autoConstruct(rs, rn)
  }
}

