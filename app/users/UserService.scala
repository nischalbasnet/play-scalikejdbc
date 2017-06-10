package users

import javax.inject.{Inject, Singleton}

import scalikejdbc.AutoSession
import users.dao.IUserDAO
import users.models.{User, UserUpdateForm}

/**
  * Created by nbasnet on 6/7/17.
  */
@Singleton
class UserService @Inject()(
  userDAO: IUserDAO
) extends IUserService
{
  implicit val session = AutoSession
  //  implicit val session = StringAutoSession

  /**
    * Get user method
    *
    * @param user_id
    *
    * @return
    */
  def get(user_id: String): Option[User] =
  {
    userDAO.get(user_id)
  }

  /**
    * Get user by email
    *
    * @param email
    *
    * @return
    */
  def getByEmail(email: String): Option[User] =
  {
    userDAO.getFor(email = Some(email)).headOption
  }

  /**
    * perform user password change
    *
    * @param user
    * @param newPassword
    *
    * @return
    */
  def changeUsersPassword(user: User, newPassword: String): User =
  {
    val (encryptedPassword, generatedSalt) = encryptPassword(newPassword)
    val success = userDAO.changeUsersPassword(user, encryptedPassword, generatedSalt)

    if (success == 1) user.syncOriginal()
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

  /**
    * create new user
    *
    * @param user
    *
    * @return
    */
  def createUser(user: User): User =
  {
    val newUserId = userDAO.save(user, None)
    user.copy(user_id = newUserId)
  }

  /**
    * update user
    *
    * @param user_id
    * @param userInfo
    *
    * @return
    */
  def updateUser(user_id: String, userInfo: UserUpdateForm): User =
  {
    val success = userDAO.save(user_id, userInfo)
    //get the update user now
    userDAO.getOrFail(user_id)
  }
}

trait IUserService
{
  def get(user_id: String): Option[User]

  def getByEmail(email: String): Option[User]

  def changeUsersPassword(user: User, newPassword: String): User

  def createUser(user: User): User

  def updateUser(user_id: String, userInfo: UserUpdateForm): User
}