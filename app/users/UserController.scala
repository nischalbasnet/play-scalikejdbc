package users

import javax.inject.{Inject, Singleton}

import com.nischal.base.BaseController
import com.nischal.base.NormalizedResponse.{jsonFail, jsonOk}
import play.api.libs.json.Json
import play.api.mvc._
import users.dao.IUserDAO
import users.models.{User, UserAddress}

/**
  * Created by nbasnet on 6/4/17.
  */
@Singleton
class UserController @Inject()(
  userService: IUserService,
  implicit val userDAO: IUserDAO
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
      case Some(u: User) => Ok(jsonOk(u.toJson()))
      case _ => NotFound(jsonFail(message = "User not found"))
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

        Ok(jsonOk(updatedUser.toJson()))
      }
      case _ => NotFound(jsonFail(message = "User not found"))
    }
  }

  /**
    * Get user friends
    *
    * @param user_id
    *
    * @return
    */
  def getFriends(user_id: String) = Action {
    userService.get(user_id) match {
      case Some(u: User) => Ok(
        jsonOk(Json.toJson(u.friends))
      )
      case _ => NotFound(jsonFail(message = "User not found"))
    }
  }

  /**
    *
    * @param user_id
    *
    * @return
    */
  def getAddresses(user_id: String) = Action {
    userService.get(user_id) match {
      case Some(u: User) => Ok(
        jsonOk(UserAddress.toJson(u.addresses())(UserAddress.withAddress))
      )
      case _ => NotFound(jsonFail(message = "User not found"))
    }
  }
}
