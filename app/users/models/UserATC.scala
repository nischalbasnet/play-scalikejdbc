package users.models

import play.api.libs.json.Json
import scalikejdbc.ParameterBinder
import scalikejdbc.interpolation.SQLSyntax

/**
  * Created by nbasnet on 6/5/17.
  */
trait UserATC
{
  self: User =>

  def toJson() = Json.toJson(this)

  protected val _updateForm: UserUpdateForm = UserUpdateForm()

  def setFirstName(inFirstName: String) =
  {
    _updateForm.first_name = Some(inFirstName)
    this
  }

  def setLastName(inLastName: String) =
  {
    _updateForm.last_name = Some(inLastName)
    this
  }

  def setEmail(inEmail: String) =
  {
    _updateForm.email = Some(inEmail)
    this
  }

  def setMobileNumber(inMobileNumber: String) =
  {
    _updateForm.mobile_number = Some(inMobileNumber)
    this
  }

  def setPassword(inPassword: String) =
  {
    _updateForm.password = Some(inPassword)
    this
  }

  def setSalt(inSalt: String) =
  {
    _updateForm.salt = Some(inSalt)
    this
  }

  def setGenderId(inGenderId: String) =
  {
    _updateForm.gender_id = Some(inGenderId)
    this
  }
}

case class UserUpdateForm(
  var first_name: Option[String] = None,
  var last_name: Option[String] = None,
  var email: Option[String] = None,
  var mobile_number: Option[String] = None,
  var image: Option[String] = None,
  var password: Option[String] = None,
  var salt: Option[String] = None,
  var gender_id: Option[String] = None
)
{
  def updateValuesMap: Map[SQLSyntax, ParameterBinder] = ???
}