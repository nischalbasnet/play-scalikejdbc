package modelservices.users.dao

import com.nischal.base.RelationDetail
import com.nischal.basecontracts.{IBaseReadDAO, IBaseWriteDAO}
import modelservices.users.UserEntity
import modelservices.users.models.{Gender, User, UserAddress, UserUpdateForm}
import scalikejdbc.DBSession
import services.events.ModelEvent

/**
  * Created by nbasnet on 6/4/17.
  */
trait IUserDAO extends IUserReadDAO with IUserWriteDAO
{
  def getEntity(primaryId: String)(implicit session: DBSession): Option[UserEntity]

  def getEntityOrFail(primaryId: String)(implicit session: DBSession): UserEntity
}

trait IUserReadDAO extends IBaseReadDAO[User, String]
{

  def getWith(primaryId: String, relations: Seq[RelationDetail[_, _, _]])(implicit session: DBSession): Option[User]

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
  )(implicit session: DBSession): Seq[User]

  /**
    * Get Users gender
    *
    * @param user_id
    * @param session
    * @return
    */
  def getUsersGender(user_id: String)(implicit session: DBSession): Option[Gender]

  /**
    *
    * @param user_id
    * @param session
    * @return
    */
  def getFriends(user_id: String)(implicit session: DBSession): Seq[User]

  /**
    *
    * @param user_id
    * @return
    */
  def getAddresses(user_id: String)(implicit session: DBSession): scala.Seq[UserAddress]
}

trait IUserWriteDAO extends IBaseWriteDAO[User, String]
{
  /**
    * Change modelservices.users password
    *
    * @param user
    * @param newPassword
    * @param salt
    * @param session
    * @return
    */
  def changeUsersPassword(user: User, newPassword: String, salt: String)(implicit session: DBSession): Int

  def saveForm(user_id: String, updateForm: UserUpdateForm)(implicit session: DBSession): Int
}

trait IUserDbDAO extends IUserReadDAO with IUserWriteDAO
{
  def modelEventBus: ModelEvent[User]
}