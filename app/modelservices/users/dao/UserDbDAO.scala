package modelservices.users.dao

import javax.inject.Singleton

import modelservices.address.models.Address
import com.nischal.base.BaseDbDAO
import com.nischal.exceptions.ModelNotFound
import play.api.libs.json.Json
import scalikejdbc._
import services.events.ModelEvent
import modelservices.users.UserMO
import modelservices.users.models.UserRelations.UserRelations
import modelservices.users.models._

/**
  * Created by nbasnet on 6/4/17.
  */
@Singleton
class UserDbDAO extends BaseDbDAO[User] with IUserDbDAO
{
  override val modelEventBus: ModelEvent[User] = new ModelEvent[User]()

  override val modelObserver = Some(UserMO())

  /**
    * Companion class for model object
    *
    * @return
    */
  override val modelCompanion = User

  /**
    * Change modelservices.users password
    *
    * @param user
    * @param newPassword
    * @param salt
    * @param session
    *
    * @return
    */
  def changeUsersPassword(user: User, newPassword: String, salt: String)(implicit session: DBSession): Int =
  {
    user.setPassword(newPassword).setSalt(salt)

    performModelUpdate(user, user.user_id)
  }

  /**
    * General get method
    *
    * @param first_name
    * @param last_name
    * @param email
    * @param mobile_number
    * @param gender_id
    * @param session
    *
    * @return
    */
  def getFor(
    first_name: Option[String] = None,
    last_name: Option[String] = None,
    email: Option[String] = None,
    mobile_number: Option[String] = None,
    gender_id: Option[String] = None
  )(implicit session: DBSession): Seq[User] =
  {
    val u = User.defaultTable
    withSQL {
      select
        .from(User as u)
        .where(sqls.toAndConditionOpt(
          first_name.map(sqls.eq(u.first_name, _)),
          last_name.map(sqls.eq(u.last_name, _)),
          email.map(sqls.eq(u.email, _)),
          mobile_number.map(sqls.eq(u.mobile_number, _)),
          gender_id.map(sqls.eq(u.gender_id, _))
        ))
    }.map(User.fromSqlResult(_))
      .list().apply()
  }

  /**
    * Get modelservices.users gender
    *
    * @param user_gender_id
    * @param session
    *
    * @return
    */
  def getUsersGender(user_gender_id: String)(implicit session: DBSession): Option[Gender] =
  {
    val g = Gender.defaultTable
    val u = User.defaultTable

    //    withSQL {
    //      queryUserGender(user_id, g, u)
    //    }.map(Gender.fromSqlResult(g.resultName)(_))
    //      .single().apply()

    getRelation[Gender, Nothing](user_gender_id, UserRelationShips.GENDER, Gender).headOption
  }

  /**
    * Query to get modelservices.users gender
    *
    * @param user_id
    * @param g
    * @param u
    *
    * @return
    */
  private def queryUserGender[A](
    user_id: String,
    g: Gender.SQLSyntaxT[Gender] = Gender.defaultTable,
    u: User.SQLSyntaxT[User] = User.defaultTable,
    aliasedResultName: Boolean = true
  ): scalikejdbc.SQLBuilder[A] =
  {
    select(if (aliasedResultName) g.resultAll else g.*)
      .from(Gender as g)
      .join(User as u)
      .on(u.gender_id, g.gender_id)
      .where.eq(u.user_id, user_id)
      .and.isNull(g.soft_deleted)
  }

  /**
    *
    * @param user_id
    * @param session
    *
    * @return
    */
  def getFriends(user_id: String)(implicit session: DBSession): Seq[User] =
  {
    getRelation[User, Friend](user_id, UserRelationShips.FRIENDS, User)
  }

  private def queryUserFriends[A](
    user_id: String,
    u: User.SQLSyntaxT[User] = User.defaultTable,
    f: Friend.SQLSyntaxT[Friend] = Friend.defaultTable,
    aliasedResultName: Boolean = true
  ): scalikejdbc.SQLBuilder[A] =
  {
    select(if (aliasedResultName) u.resultAll else u.*)
      .from(Friend as f)
      .join(User as u)
      .on(u.user_id, f.friend_user_id)
      .where.eq(f.user_id, user_id)
      .and.isNull(f.soft_deleted)
  }

  /**
    *
    * @param user_id
    *
    * @return
    */
  def getAddresses(user_id: String)(implicit session: DBSession): Seq[UserAddress] =
  {

    withSQL {
      queryUserAddresses(user_id)
    }.map(rs => {
      val userAddress = UserAddress.fromSqlResult(rs)
      val address = Address.fromSqlResult(rs)
      userAddress.setAddress(address)

      userAddress
    }).list()
      .apply()

    //        getRelation[Address, UserAddress](user_id, UserRelationShips.ADDRESS, Address)
  }

  private def queryUserAddresses[A](
    user_id: String,
    a: Address.SQLSyntaxT[Address] = Address.defaultTable,
    ua: UserAddress.SQLSyntaxT[UserAddress] = UserAddress.defaultTable,
    aliasedResultName: Boolean = true
  ): scalikejdbc.SQLBuilder[A] =
  {
    val selectField: scalikejdbc.SelectSQLBuilder[A] = if (aliasedResultName) select(ua.resultAll, a.resultAll)
    else select(ua.*, a.*)

    selectField
      .from(UserAddress as ua)
      .join(Address as a)
      .on(a.address_id, ua.address_id)
      .where.eq(ua.user_id, user_id)
      .and.isNull(ua.soft_deleted)
  }

  /**
    *
    * @param user_id
    * @param updateForm
    *
    * @return
    */
  def save(user_id: String, updateForm: UserUpdateForm)(implicit session: DBSession): Int =
  {
    performUpdate(user_id, updateForm.updateValuesMap)
  }
}
