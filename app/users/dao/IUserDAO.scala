package users.dao

import com.nischal.basecontracts.{IBaseReadDAO, IBaseWriteDAO}
import scalikejdbc.DBSession
import users.models.User

/**
  * Created by nbasnet on 6/4/17.
  */
trait IUserDAO extends IUserReadDAO with IUserWriteDAO

trait IUserReadDAO extends IBaseReadDAO[User, String]
{
  def getByEmail(email: String)(implicit session: DBSession): Option[User]
}

trait IUserWriteDAO extends IBaseWriteDAO[User, String]
{
  def changeUsersPassword(user: User, newPassword: String, salt: String)(implicit session: DBSession): Int
}

trait IUserPostgresDAO extends IUserReadDAO with IUserWriteDAO