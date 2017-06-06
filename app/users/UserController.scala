package users

import javax.inject.{Inject, Singleton}

import com.nischal.base.BaseController
import play.api.mvc._
import scalikejdbc.AutoSession
import users.dao.IUserDAO
import users.models.User

/**
  * Created by nbasnet on 6/4/17.
  */
@Singleton
class UserController @Inject()(
  userDAO: IUserDAO
) extends BaseController
{
  implicit val session = AutoSession

  /**
    * Get user
    *
    * @param user_id
    *
    * @return
    */
  def get(user_id: String) = Action {
    userDAO.get(user_id) match {
      case Some(u: User) => Ok(u.toJson())
      case _ => NotFound("User not found")
    }
  }
}
