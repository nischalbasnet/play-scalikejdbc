package users.dao

import javax.inject.{Inject, Singleton}

import scalikejdbc.{DBSession, NamedAutoSession}
import users.models.{Gender, User, UserAddress}

/**
  * Created by nbasnet on 6/4/17.
  */
@Singleton
class UserDAO @Inject()(
  userPostgresDAO: IUserPostgresDAO
) extends IUserDAO
{
  val writeSession: DBSession = NamedAutoSession("write")

  /**
    * Get User
    *
    * @param primaryId
    * @param session
    *
    * @return
    */
  def get(primaryId: String)(implicit session: DBSession): Option[User] =
  {
    userPostgresDAO.get(primaryId)
  }

  /**
    * Get Users
    *
    * @param primaryIds
    * @param session
    *
    * @return
    */
  def getMany(primaryIds: Seq[String])(implicit session: DBSession): Seq[User] =
  {
    userPostgresDAO.getMany(primaryIds)
  }

  /**
    * Get User or throw exception
    *
    * @param primaryId
    * @param session
    *
    * @return
    */
  def getOrFail(primaryId: String)(implicit session: DBSession): User =
  {
    userPostgresDAO.getOrFail(primaryId)
  }

  /**
    * Save User
    *
    * @param model
    * @param primaryId
    * @param session
    *
    * @return
    */
  def save(model: User, primaryId: Option[String])(implicit session: DBSession = writeSession): String =
  {
    userPostgresDAO.save(model, primaryId)
  }

  /**
    * Save Users
    *
    * @param model
    * @param primaryId
    * @param session
    *
    * @return
    */
  def saveMany(model: Seq[User], primaryId: Seq[String])(implicit session: DBSession): Seq[String] =
  {
    userPostgresDAO.saveMany(model, primaryId)
  }

  /**
    * Change Users password
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
    userPostgresDAO.changeUsersPassword(user, newPassword, salt)
  }

  /**
    * Get user for different condition
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
    userPostgresDAO.getFor(first_name, last_name, email, mobile_number, gender_id)
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
    userPostgresDAO.getUsersGender(user_id)
  }

  /**
    *
    * @param user_id
    * @param session
    *
    * @return
    */
  override def getFriends(user_id: String)(implicit session: DBSession): Seq[User] =
  {
    userPostgresDAO.getFriends(user_id)
  }

  /**
    *
    * @param user_id
    *
    * @return
    */
  def getAddresses(user_id: String)(implicit session: DBSession): Seq[UserAddress] =
  {
    userPostgresDAO.getAddresses(user_id)
  }
}
