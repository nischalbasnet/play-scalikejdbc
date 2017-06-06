package users.dao

import javax.inject.{Inject, Singleton}

import scalikejdbc.{AutoSession, DBSession}
import users.models.User

/**
  * Created by nbasnet on 6/4/17.
  */
@Singleton
class UserDAO @Inject()(
  userPostgresDAO: IUserPostgresDAO
) extends IUserDAO
{
  def get(primaryId: String)(implicit session: DBSession = AutoSession): Option[User] =
  {
    userPostgresDAO.get(primaryId)
  }

  def getMany(primaryIds: Seq[String])(implicit session: DBSession = AutoSession): Seq[User] =
  {
    userPostgresDAO.getMany(primaryIds)
  }

  def getOrFail(primaryId: String)(implicit session: DBSession = AutoSession): User =
  {
    userPostgresDAO.getOrFail(primaryId)
  }

  def save(model: User, primaryId: Option[String])(implicit session: DBSession = AutoSession): String =
  {
    userPostgresDAO.save(model, primaryId)
  }

  def saveMany(model: Seq[User], primaryId: Seq[String])(implicit session: DBSession = AutoSession): Seq[String] =
  {
    userPostgresDAO.saveMany(model, primaryId)
  }
}
