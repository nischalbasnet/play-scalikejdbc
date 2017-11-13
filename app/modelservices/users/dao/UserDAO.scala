package modelservices.users.dao

import javax.inject.{Inject, Singleton}

import com.nischal.base.RelationDetail
import modelservices.users.UserEntity
import modelservices.users.models.{Gender, User, UserAddress, UserUpdateForm}
import scalikejdbc.{DBSession, NamedAutoSession}

/**
  * Created by nbasnet on 6/4/17.
  */
@Singleton
class UserDAO @Inject()(
  userDbDAO: IUserDbDAO
) extends IUserDAO
{
  val defaultSession: DBSession = userDbDAO.defaultSession

  val writeSession: DBSession = NamedAutoSession('write)

  /**
    * Get User
    *
    * @param primaryId
    * @param session
    * @return
    */
  def get(primaryId: String)(implicit session: DBSession = defaultSession): Option[User] =
  {
    userDbDAO.get(primaryId)
  }

  /**
    * Get user entity
    *
    * @param primaryId
    * @param session
    * @return
    */
  def getEntity(primaryId: String)(implicit session: DBSession = defaultSession): Option[UserEntity] =
  {
    get(primaryId) match {
      case Some(u: User) => Some(UserEntity(u, this))
      case _ => None
    }
  }

  /**
    * Get user entity or fail with exception
    *
    * @param primaryId
    * @param session
    * @return
    */
  def getEntityOrFail(primaryId: String)(implicit session: DBSession = defaultSession): UserEntity =
  {
    UserEntity(getOrFail(primaryId), this)
  }

  /**
    * Get Users
    *
    * @param primaryIds
    * @param session
    * @return
    */
  def getMany(primaryIds: Seq[String])(implicit session: DBSession = defaultSession): Seq[User] =
  {
    userDbDAO.getMany(primaryIds)
  }

  /**
    * Get User or throw exception
    *
    * @param primaryId
    * @param session
    * @return
    */
  def getOrFail(primaryId: String)(implicit session: DBSession = defaultSession): User =
  {
    userDbDAO.getOrFail(primaryId)
  }

  /**
    * Change Users password
    *
    * @param user
    * @param newPassword
    * @param salt
    * @param session
    * @return
    */
  def changeUsersPassword(user: User, newPassword: String, salt: String)(implicit session: DBSession = defaultSession): Int =
  {
    userDbDAO.changeUsersPassword(user, newPassword, salt)
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
    userDbDAO.getFor(first_name, last_name, email, mobile_number, gender_id)
  }

  /**
    * Get modelservices.users gender
    *
    * @param user_id
    * @param session
    * @return
    */
  def getUsersGender(user_id: String)(implicit session: DBSession = defaultSession): Option[Gender] =
  {
    userDbDAO.getUsersGender(user_id)
  }

  /**
    *
    * @param user_id
    * @param session
    * @return
    */
  override def getFriends(user_id: String)(implicit session: DBSession = defaultSession): Seq[User] =
  {
    userDbDAO.getFriends(user_id)
  }

  /**
    *
    * @param user_id
    * @return
    */
  def getAddresses(user_id: String)(implicit session: DBSession = defaultSession): Seq[UserAddress] =
  {
    userDbDAO.getAddresses(user_id)
  }


  /**
    * Save User
    *
    * @param model
    * @param primaryId
    * @param session
    * @return
    */
  def save(model: User, primaryId: Option[String])(implicit session: DBSession = writeSession): String =
  {
    userDbDAO.save(model, primaryId)
  }

  /**
    *
    * @param user_id
    * @param updateForm
    * @return
    */
  def saveForm(user_id: String, updateForm: UserUpdateForm)(implicit session: DBSession = writeSession): Int =
  {
    userDbDAO.saveForm(user_id, updateForm)
  }

  /**
    * Save Users
    *
    * @param model
    * @param primaryId
    * @param session
    * @return
    */
  def saveMany(model: Seq[User], primaryId: Seq[String])(implicit session: DBSession = defaultSession): Seq[String] =
  {
    userDbDAO.saveMany(model, primaryId)
  }

  def getWith(user_id: String, relations: Seq[RelationDetail[_, _, _]])(implicit session: DBSession = defaultSession): Option[User] =
  {
    userDbDAO.getWith(user_id, relations)
  }
}
