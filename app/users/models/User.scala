package users.models

import javax.inject.Singleton

import com.nischal.base.{BaseModel, BaseModelCompanion}
import org.joda.time.DateTime
import play.api.libs.json.{Json, Reads, Writes}
import scalikejdbc.interpolation.SQLSyntax
import scalikejdbc.{ParameterBinder, WrappedResultSet, autoConstruct}

/**
  * Created by nbasnet on 6/4/17.
  */
case class User(
  user_id: String,
  first_name: String,
  last_name: String,
  email: String,
  mobile_number: String,
  image: String,
  password: String,
  salt: String,
  gender_id: String,
  created: DateTime,
  updated: DateTime,
  soft_deleted: Option[DateTime]
) extends BaseModel with UserATC
{
  override def insertValuesMap: Map[SQLSyntax, ParameterBinder] = ???

  override def updateValuesMap: Map[SQLSyntax, ParameterBinder] = _updateForm.updateValuesMap
}

object User extends BaseModelCompanion[User]
{
  override val tableName = "users"

  override val primaryKey: String = "user_id"

  override val archivedField: Option[String] = Some("soft_deleted")

  override def fromSqlResult(rn: scalikejdbc.ResultName[User])(rs: WrappedResultSet): User = autoConstruct(rs, rn)

  implicit val reads: Reads[User] = Json.format[User]
  implicit val writes: Writes[User] = Json.format[User]
}

@Singleton
class UsersCompanion extends BaseModelCompanion[User]
{
  override val tableName = "users"

  override val primaryKey: String = "user_id"

  override val archivedField: Option[String] = Some("soft_deleted")

  override def fromSqlResult(rn: scalikejdbc.ResultName[User])(rs: WrappedResultSet): User = autoConstruct(rs, rn)
}