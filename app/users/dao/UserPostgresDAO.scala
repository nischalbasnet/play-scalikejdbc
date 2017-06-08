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
    *
    * @param email
    * @param session
    *
    * @return
    */
  def getByEmail(email: String)(implicit session: DBSession): Option[User] =
  {
    val u = modelCompanion.syntax("u")
    withSQL {
      select
        .from(User as u)
        .where.eq(u.email, email)
    }.map(modelCompanion.fromSqlResult(u.resultName)(_))
      .single()
      .apply()
  }
}
