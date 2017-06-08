package users

import javax.inject.{Inject, Singleton}

import com.nischal.base.BaseController
import play.api.mvc._
import users.models.User

/**
  * Created by nbasnet on 6/4/17.
  */
@Singleton
class UserController @Inject()(
  userService: IUserService
) extends BaseController
{
  /**
    * Get user
    *
    * @param user_id
    *
    * @return
    */
  def get(user_id: String) = Action {
    userService.get(user_id) match {
      case Some(u: User) => Ok(u.toJson())
      case _ => NotFound("User not found")
    }
  }

  /**
    *
    * @param email
    * @param new_password
    *
    * @return
    */
  def changePassword(email: String, new_password: String) = Action {
    userService.getByEmail(email) match {
      case Some(user: User) => {
        val updatedUser = userService.changeUsersPassword(user, new_password)

        Ok(updatedUser.toJson())
      }
      case _ => NotFound("User not found")
    }
  }
}
