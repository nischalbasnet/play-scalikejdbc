package controllers

import javax.inject.{Inject, Singleton}

import com.nischal.base.BaseController
import com.nischal.base.NormalizedResponse.{jsonFail, jsonOk}
import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._
import modelservices.users.IUserService
import modelservices.users.dao.IUserDAO
import modelservices.users.models.{User, UserAddress, UserUpdateForm}

/**
  * Created by nbasnet on 6/4/17.
  */
@Singleton
class UserController @Inject()(
  userService: IUserService,
  cc: ControllerComponents
)(
  implicit val userDAO: IUserDAO
) extends BaseController(cc)
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

  val userCreateRequest = Form(
    mapping(
      "user_id" -> ignored(fakeId),
      "first_name" -> nonEmptyText,
      "last_name" -> nonEmptyText,
      "email" -> nonEmptyText,
      "mobile_number" -> optional(text),
      "image" -> ignored("default.png"),
      "password" -> optional(text),
      "salt" -> optional(text),
      "gender_id" -> optional(text),
      "created" -> ignored(DateTime.now()),
      "updated" -> ignored(DateTime.now()),
      "soft_deleted" -> ignored(Option(DateTime.now()))
    )(User.apply)(User.unapply)
  )

  /**
    * create user record
    *
    * @return
    */
  def create() = Action(parse.form(
    userCreateRequest, onErrors = (error: Form[User]) => {
      BadRequest(handleRequestError(error))
    })
  ) { implicit request =>
    val newUser = userService.createUser(request.body)

    Ok(jsonOk(newUser.toJson()))
  }

  val userUpdateRequest = Form(
    mapping(
      "first_name" -> optional(text),
      "last_name" -> optional(text),
      "email" -> optional(text),
      "mobile_number" -> optional(text),
      "image" -> optional(text),
      "password" -> optional(text),
      "salt" -> optional(text),
      "gender_id" -> optional(text),
      "updated" -> ignored(Option(DateTime.now())),
      "soft_deleted" -> ignored(Option(DateTime.now()))
    )(UserUpdateForm.apply)(UserUpdateForm.unapply)
  )

  /**
    * Update user
    *
    * @param user_id
    *
    * @return
    */
  def update(user_id: String) = Action(parse.form(
    userUpdateRequest, onErrors = (error: Form[UserUpdateForm]) => {
      BadRequest(handleRequestError(error))
    })
  ) { implicit request =>
    val updateRequest = request.body.copy(soft_deleted = None)

    val updateUser = userService.updateUser(user_id, updateRequest)
    Ok(jsonOk(data = updateUser.toJson(), message = "User update"))
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

  def getGender(user_id: String) = Action {
    userService.get(user_id) match {
      case Some(u: User) => Ok(
        jsonOk(Json.toJson(u.gender))
      )
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
