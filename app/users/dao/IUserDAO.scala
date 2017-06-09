package users.dao

import com.nischal.basecontracts.{IBaseReadDAO, IBaseWriteDAO}
import scalikejdbc.DBSession
import users.models.{Gender, User}

/**
  * Created by nbasnet on 6/4/17.
  */
trait IUserDAO extends IUserReadDAO with IUserWriteDAO

trait IUserReadDAO extends IBaseReadDAO[User, String]
{
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
  )(implicit session: DBSession): Seq[User]

  /**
    * Get Users gender
    *
    * @param user_id
    * @param session
    *
    * @return
    */
  def getUsersGender(user_id: String)(implicit session: DBSession): Option[Gender]
}

trait IUserWriteDAO extends IBaseWriteDAO[User, String]
{
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
  def changeUsersPassword(user: User, newPassword: String, salt: String)(implicit session: DBSession): Int
}

trait IUserPostgresDAO extends IUserReadDAO with IUserWriteDAO