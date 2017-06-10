package users.models

import org.joda.time.DateTime
import scalikejdbc.ParameterBinder
import scalikejdbc.interpolation.SQLSyntax

/**
  * Created by nbasnet on 6/5/17.
  */
trait UserATC
{
  self: User =>

  protected val _updateForm: UserUpdateForm = UserUpdateForm()

  def insertValuesMap: Map[SQLSyntax, ParameterBinder] =
  {
    val table = User.column
    val insertMap: Map[SQLSyntax, ParameterBinder] = Map(
      table.column("first_name") -> first_name,
      table.column("last_name") -> last_name,
      table.column("email") -> email,
      table.column("mobile_number") -> mobile_number,
      table.column("image") -> image,
      table.column("password") -> password,
      table.column("salt") -> salt,
      table.column("gender_id") -> gender_id,
      table.column("created") -> created,
      table.column("updated") -> updated
    )

    insertMap
  }

  def updateValuesMap: Map[SQLSyntax, ParameterBinder] = _updateForm.updateValuesMap

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

  def setImage(inImage: String) =
  {
    _updateForm.image = Some(inImage)
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

  def setUpdated(inUpdated: DateTime) =
  {
    _updateForm.updated = Some(inUpdated)
    this
  }

  def setSoftDeleted(inSoftDeleted: DateTime) =
  {
    _updateForm.soft_deleted = Some(inSoftDeleted)
    this
  }

  def syncOriginal() =
  {
    this.copy(
      first_name = _updateForm.first_name.getOrElse(first_name),
      last_name = _updateForm.last_name.getOrElse(last_name),
      email = _updateForm.email.getOrElse(email),
      mobile_number = _updateForm.mobile_number match {
        case Some(s: String) => Some(s)
        case _ => mobile_number
      },
      image = _updateForm.image.getOrElse(image),
      password = _updateForm.password match {
        case Some(s: String) => Some(s)
        case _ => password
      },
      salt = _updateForm.salt match {
        case Some(s: String) => Some(s)
        case _ => salt
      },
      gender_id = _updateForm.gender_id match {
        case Some(s: String) => Some(s)
        case _ => gender_id
      },
      updated = _updateForm.updated.getOrElse(updated),
      soft_deleted = _updateForm.soft_deleted match {
        case Some(s: DateTime) => Some(s)
        case _ => soft_deleted
      }
    )
  }

}

/**
  * Users companion Object
  */
case class UserUpdateForm(
  var first_name: Option[String] = None,
  var last_name: Option[String] = None,
  var email: Option[String] = None,
  var mobile_number: Option[String] = None,
  var image: Option[String] = None,
  var password: Option[String] = None,
  var salt: Option[String] = None,
  var gender_id: Option[String] = None,
  var updated: Option[DateTime] = None,
  var soft_deleted: Option[DateTime] = None
)
{
  def updateValuesMap: Map[SQLSyntax, ParameterBinder] =
  {
    val table = User.column
    var updateMap: Map[SQLSyntax, ParameterBinder] = Map.empty

    if (first_name.isDefined) updateMap = updateMap ++ Map(table.column("first_name") -> first_name.get)

    if (last_name.isDefined) updateMap = updateMap ++ Map(table.column("last_name") -> last_name.get)

    if (email.isDefined) updateMap = updateMap ++ Map(table.column("email") -> email.get)

    if (mobile_number.isDefined) updateMap = updateMap ++ Map(table.column("mobile_number") -> mobile_number.get)

    if (image.isDefined) updateMap = updateMap ++ Map(table.column("image") -> image.get)

    if (password.isDefined) updateMap = updateMap ++ Map(table.column("password") -> password.get)

    if (salt.isDefined) updateMap = updateMap ++ Map(table.column("salt") -> salt.get)

    if (gender_id.isDefined) updateMap = updateMap ++ Map(table.column("gender_id") -> gender_id.get)

    if (updated.isDefined) updateMap = updateMap ++ Map(table.column("updated") -> updated.get)

    if (soft_deleted.isDefined) updateMap = updateMap ++ Map(table.column("soft_deleted") -> soft_deleted.get)

    updateMap
  }
}