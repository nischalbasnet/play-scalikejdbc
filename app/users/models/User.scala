package users.models

import javax.inject.Singleton

import com.nischal.base.{BaseModel, BaseModelCompanion, BaseModelRelations}
import org.joda.time.DateTime
import play.api.libs.json.{Json, Reads, Writes}
import scalikejdbc.{AutoSession, DBSession, WrappedResultSet, autoConstruct}
import users.dao.IUserDAO

/**
  * Created by nbasnet on 6/4/17.
  */
case class User(
  user_id: String,
  first_name: String,
  last_name: String,
  email: String,
  mobile_number: Option[String],
  image: String,
  password: Option[String],
  salt: Option[String],
  gender_id: Option[String],
  created: DateTime,
  updated: DateTime,
  soft_deleted: Option[DateTime]
) extends BaseModel[User] with UserATC with UserRelationDefinitions
{
  protected var _genderSetOnly: Option[Gender] = _
  protected var _friendsSetOnly: Seq[User] = _
  protected var _addressesSetOnly: Seq[UserAddress] = _
}

object User extends UserCompanionInfo
{
  implicit val reads: Reads[User] = Json.format[User]
  implicit val writes: Writes[User] = Json.format[User]
}

/**
  * This class is required by the base model class to give curd ability to User model class
  */
@Singleton
class UsersCompanion extends UserCompanionInfo

/**
  * Contains info the BaseModelCompanion
  */
trait UserCompanionInfo extends BaseModelCompanion[User]
{
  override val defaultTable: SQLSyntaxT[User] = syntax("u")

  override val tableName = "users"

  override val primaryKey: String = "user_id"

  override val archivedField: Option[String] = Some("soft_deleted")

  override def fromSqlResult(rn: scalikejdbc.ResultName[User])(rs: WrappedResultSet): User = autoConstruct(rs, rn)
}

/**
  * Trait that contains the relations for user
  */
trait UserRelationDefinitions
{
  self: User =>

  /**
    * Get users genders
    *
    * @param userDAO
    * @param session
    *
    * @return
    */
  def gender()(implicit userDAO: IUserDAO, session: DBSession = AutoSession): Option[Gender] =
  {
    if (_genderSetOnly == null) {
      _genderSetOnly = userDAO.getUsersGender(user_id)
    }
    _genderSetOnly
  }

  /**
    * Get friends
    *
    * @param userDAO
    * @param session
    *
    * @return
    */
  def friends()(implicit userDAO: IUserDAO, session: DBSession = AutoSession): Seq[User] =
  {
    if (_friendsSetOnly == null) {
      _friendsSetOnly = userDAO.getFriends(user_id)
    }
    _friendsSetOnly
  }

  def addresses()(implicit userDAO: IUserDAO, session: DBSession = AutoSession): Seq[UserAddress] =
  {
    if (_addressesSetOnly == null) {
      _addressesSetOnly = userDAO.getAddresses(user_id)
    }
    _addressesSetOnly
  }

  /**
    * RELATION setters
    */

  /**
    * Set Gender
    *
    * @param gender
    */
  def setGender(gender: Option[Gender]): User =
  {
    _genderSetOnly = gender
    this
  }

  /**
    * set address
    *
    * @param addresses
    */
  def setAddresses(addresses: Seq[UserAddress]): User =
  {
    _addressesSetOnly = addresses
    this
  }

  /**
    * set friends
    *
    * @param friends
    */
  def setFriends(friends: Seq[User]): User =
  {
    _friendsSetOnly = friends
    this
  }
}

/**
  * Enum to define user relations
  */
object UserRelations extends BaseModelRelations
{
  type UserRelations = Value
  val GENDER, ADDRESS, FRIENDS = Value
}