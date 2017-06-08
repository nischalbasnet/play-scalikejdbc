package users.dao

import javax.inject.{Inject, Singleton}

import scalikejdbc.{AutoSession, DBSession, NamedAutoSession, ReadOnlyNamedAutoSession}
import users.models.User

/**
  * Created by nbasnet on 6/4/17.
  */
@Singleton
class UserDAO @Inject()(
  userPostgresDAO: IUserPostgresDAO
) extends IUserDAO
{
  val writeSession: DBSession = NamedAutoSession("write")

  def get(primaryId: String)(implicit session: DBSession): Option[User] =
  {
    userPostgresDAO.get(primaryId)
  }

  def getMany(primaryIds: Seq[String])(implicit session: DBSession): Seq[User] =
  {
    userPostgresDAO.getMany(primaryIds)
  }

  def getOrFail(primaryId: String)(implicit session: DBSession): User =
  {
    userPostgresDAO.getOrFail(primaryId)
  }

  def save(model: User, primaryId: Option[String])(implicit session: DBSession): String =
  {
    userPostgresDAO.save(model, primaryId)(writeSession)
  }

  def saveMany(model: Seq[User], primaryId: Seq[String])(implicit session: DBSession): Seq[String] =
  {
    userPostgresDAO.saveMany(model, primaryId)
  }

  def changeUsersPassword(user: User, newPassword: String, salt: String)(implicit session: DBSession): Int =
  {
    userPostgresDAO.changeUsersPassword(user, newPassword, salt)
  }

  def getByEmail(email: String)(implicit session: DBSession): Option[User] =
  {
    userPostgresDAO.getByEmail(email)
  }
}
