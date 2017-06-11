package users.dao

import javax.inject.{Inject, Singleton}

import address.models.Address
import com.nischal.base.BaseDbDAO
import com.nischal.exceptions.ModelNotFound
import play.api.libs.json.Json
import scalikejdbc._
import users.models.UserRelations.UserRelations
import users.models._

/**
  * Created by nbasnet on 6/4/17.
  */
@Singleton
class UserDbDAO @Inject()(
  usersCompanion: UsersCompanion
) extends BaseDbDAO[User, User, UsersCompanion] with IUserDbDAO
{
  /**
    * Needed as pattern match on generic T does not work
    *
    * @param optionModel
    * @param primaryId
    *
    * @return
    */
  override def modelFailMatch(optionModel: Option[User], primaryId: String): User = optionModel match {
    case Some(model: User) => model
    case None => throw ModelNotFound(modelCompanion.tableName, modelCompanion.primaryKey, primaryId)
  }

  /**
    * Companion class for model object
    *
    * @return
    */
  override val modelCompanion: UsersCompanion = usersCompanion

  def getWith(user_id: String, relations: Seq[UserRelations])(implicit session: DBSession): User =
  {
    val user = User.defaultTable

    var selectFields = sqls"SELECT ${user.resultAll}"
    var relationJoins = sqls""
    val userFilter =
      sqls"""
            WHERE ${user.user_id} = $user_id
            AND ${user.soft_deleted} ISNULL
            GROUP BY ${user.user_id}
          """

    //load the relations
    relations.foreach {
      case UserRelations.ADDRESS => {
        val a = Address.defaultTable
        val ua = UserAddress.defaultTable

        //add to select field
        selectFields = selectFields.append(
          sqls""",json_agg(row_to_json(ua.*)) as address """
        )
        relationJoins = relationJoins.append(
          sqls"""
                LEFT JOIN (
                ${queryUserAddresses(user_id, a, ua, aliasedResultName = false).toSQLSyntax}
                ) ua
                ON ua.user_id = ${user.user_id}
              """
        )
      }
      case UserRelations.FRIENDS => {
        val friends = Friend.defaultTable
        //        queryUserFriends(user_id, user, friends)
        //add to select field
        selectFields = selectFields.append(
          sqls""",json_agg(row_to_json(f.*)) as friends """
        )
        relationJoins = relationJoins.append(
          sqls"""
                LEFT JOIN (
                ${queryUserFriends(user_id, user, friends, aliasedResultName = false).toSQLSyntax}
                ) f
                ON f.soft_deleted ISNULL
              """
        )
      }
      case UserRelations.GENDER => {
        val gender = Gender.defaultTable
        queryUserGender(user_id, gender, user)
        //add to select field
        selectFields = selectFields.append(
          sqls""",json_agg(row_to_json(g.*)) as gender """
        )

        relationJoins = relationJoins.append(
          sqls"""
                LEFT JOIN (
                ${queryUserGender(user_id, gender, user, aliasedResultName = false).toSQLSyntax}
                ) g
                ON g.gender_id = ${user.gender_id}
              """
        )
      }
    }

    val fullQuery =
      sql"""
           $selectFields
            FROM ${User as user}
              $relationJoins
            $userFilter
         """

    val userInfo = fullQuery.map(rs => {
      val userAddress = Json.parse(rs.string("address")).as[Seq[UserAddress]]
      val address = Json.parse(rs.string("address")).as[Seq[Address]]
      //fill address to user address
      val finalAddr = userAddress.map(ua => {
        val addr = address.find(_.address_id == ua.address_id)
        if(addr.isDefined) ua.setAddress(addr.get)
        ua
      })

      val friends = Json.parse(rs.string("friends")).as[Seq[User]]
      val gender = Json.parse(rs.string("gender")).as[Seq[Gender]]

      val usr: User = User.fromSqlResult(user.resultName)(rs)
      usr.setAddresses(finalAddr)
        .setFriends(friends)
        .setGender(gender.headOption)
      usr
    }).first().apply()

    userInfo.get
  }

  /**
    * Change users password
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
    }.map(User.fromSqlResult(u.resultName)(_))
      .list().apply()
  }

  /**
    * Get users gender
    *
    * @param user_id
    * @param session
    *
    * @return
    */
  def getUsersGender(user_id: String)(implicit session: DBSession): Option[Gender] =
  {
    val g = Gender.defaultTable
    val u = User.defaultTable

    withSQL {
      queryUserGender(user_id, g, u)
    }.map(Gender.fromSqlResult(g.resultName)(_))
      .single().apply()
  }

  /**
    * Query to get users gender
    *
    * @param user_id
    * @param g
    * @param u
    *
    * @return
    */
  private def queryUserGender[A](
    user_id: String,
    g: Gender.SQLSyntaxT[Gender],
    u: User.SQLSyntaxT[User],
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
    val f = Friend.defaultTable
    val u = User.defaultTable

    withSQL {
      queryUserFriends(user_id, u, f)
    }.map(User.fromSqlResult(u.resultName)(_))
      .list().apply()
  }

  private def queryUserFriends[A](
    user_id: String,
    u: User.SQLSyntaxT[User],
    f: Friend.SQLSyntaxT[Friend],
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
    val a = Address.defaultTable
    val ua = UserAddress.defaultTable

    withSQL {
      queryUserAddresses(user_id, a, ua)
    }.map(rs => {
      val userAddress = UserAddress.fromSqlResult(ua.resultName)(rs)
      val address = Address.fromSqlResult(a.resultName)(rs)
      userAddress.setAddress(address)

      userAddress
    }).list()
      .apply()

  }

  private def queryUserAddresses[A](
    user_id: String,
    a: Address.SQLSyntaxT[Address],
    ua: UserAddress.SQLSyntaxT[UserAddress],
    aliasedResultName: Boolean = true
  ): scalikejdbc.SQLBuilder[A] =
  {
    //    withSQL {
    //      select(ua.resultAll, a.resultAll)
    //        .from(UserAddress as ua)
    //        .join(Address as a)
    //        .on(a.address_id, ua.address_id)
    //        .where.eq(ua.user_id, user_id)
    //        .and.isNull(ua.soft_deleted)
    //    }

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
