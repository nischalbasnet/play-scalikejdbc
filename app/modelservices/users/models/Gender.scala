package modelservices.users.models

import javax.inject.Singleton

import com.nischal.base.{BaseModelCompanion, BaseModelRelationShips}
import modelservices.RelationDescriptions
import org.joda.time.DateTime
import play.api.libs.json.{Json, Reads, Writes}
import scalikejdbc.{WrappedResultSet, autoConstruct}
import modelservices.users.models.Gender.SQLSyntaxT

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
object Gender extends GenderCompanionInfo
{

  import com.nischal.JsonReaderWriter._

  val genderSeq = shapeless.TypeCase[Seq[Gender]]

  implicit val reads: Reads[Gender] = Json.format[Gender]
  implicit val writes: Writes[Gender] = Json.format[Gender]

  override def setModelRelation[A](model: Gender, relation: Seq[A]): Unit =
  {
    relation match {
      case _ => println(s"SETTER IS NOT DEFINED FOR => $relation")
    }
  }
}

/**
  * Contains info the BaseModelCompanion
  */
trait GenderCompanionInfo extends BaseModelCompanion[Gender]
{

  override val defaultTable: SQLSyntaxT[Gender] = this.syntax("g")

  override val primaryKey: String = "gender_id"

  override val tableName: String = "genders"

  override val archivedField: Option[String] = Some("soft_deleted")

  override def fromSqlResult(rn: scalikejdbc.ResultName[Gender])(rs: WrappedResultSet): Gender =
  {
    autoConstruct(rs, rn)
  }
}

object GenderRelationShips extends BaseModelRelationShips
{
  val USERS = Val[Gender, User, Nothing]("users", RelationDescriptions.genderUsers)
}