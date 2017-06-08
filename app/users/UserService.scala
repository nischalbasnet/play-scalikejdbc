package users

import javax.inject.{Inject, Singleton}

import org.joda.time.DateTime
import scalikejdbc.DBSession
import users.dao.IUserDAO
import users.models.User

/**
  * Created by nbasnet on 6/7/17.
  */
@Singleton
class UserService @Inject()(
  userDAO: IUserDAO
) extends IUserService
{
  /**
    * Get user method
    *
    * @param user_id
    * @param session
    *
    * @return
    */
  def get(user_id: String)(implicit session: DBSession): Option[User] =
  {
    userDAO.get(user_id)
  }

  /**
    * Get user by email
    *
    * @param email
    * @param session
    *
    * @return
    */
  def getByEmail(email: String)(implicit session: DBSession): Option[User] =
  {
    userDAO.getByEmail(email)
  }

  /**
    * perform user password change
    *
    * @param user
    * @param newPassword
    * @param session
    *
    * @return
    */
  def changeUsersPassword(user: User, newPassword: String)(implicit session: DBSession): User =
  {
    val (encryptedPassword, generatedSalt) = encryptPassword(newPassword)
    val success = userDAO.changeUsersPassword(user, encryptedPassword, generatedSalt)

    if (success == 1) user.copy(password = Some(newPassword), updated = new DateTime())
    else user
  }

  /**
    * function to encrypt password
    *
    * @param password
    *
    * @return
    */
  private def encryptPassword(password: String): (String, String) =
  {
    val generatedSalt = "new_saltxxtt"

    (password, generatedSalt)
  }
}

trait IUserService
{
  def get(user_id: String)(implicit session: DBSession): Option[User]

  def getByEmail(email: String)(implicit session: DBSession): Option[User]

  def changeUsersPassword(user: User, newPassword: String)(implicit session: DBSession): User
}