package users.dao

import javax.inject.{Inject, Singleton}

import com.nischal.base.BasePostgresDAO
import com.nischal.exceptions.ModelNotFound
import scalikejdbc._
import users.models.{User, UsersCompanion}

/**
  * Created by nbasnet on 6/4/17.
  */
@Singleton
class UserPostgresDAO @Inject()(
  usersCompanion: UsersCompanion
) extends BasePostgresDAO[User, UsersCompanion] with IUserPostgresDAO
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

  def changeUsersPassword(user: User, newPassword: String, salt: String)(implicit session: DBSession): Int =
  {
    user.setPassword(newPassword).setSalt(salt)

    save(user, Some(user.user_id))
    1 //TODO Return proper result
  }

  /**
    * General get method
    * @param first_name
    * @param last_name
    * @param email
    * @param mobile_number
    * @param gender_id
    * @param session
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
    val u = modelCompanion.defaultTable
    withSQL {
      select
        .from(modelCompanion as u)
        .where(sqls.toAndConditionOpt(
          first_name.map(sqls.eq(u.first_name, _)),
          last_name.map(sqls.eq(u.last_name, _)),
          email.map(sqls.eq(u.email, _)),
          mobile_number.map(sqls.eq(u.mobile_number, _)),
          gender_id.map(sqls.eq(u.gender_id, _))
        ))
    }.map(modelCompanion.fromSqlResult(u.resultName)(_))
      .list().apply()
  }
}
