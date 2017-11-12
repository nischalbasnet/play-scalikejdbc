package modelservices.users.models

import org.joda.time.DateTime
import scalikejdbc.interpolation.SQLSyntax
import scalikejdbc.{ParameterBinder, ParameterBinderFactory, autoNamedValues}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by nbasnet on 6/5/17.
  */
trait UserATC
{
  self: User =>

  protected var _updateForm: UserUpdateForm = UserUpdateForm()

  val insertExcludeList = Seq(
    "user_id",
    "created",
    "updated",
    "soft_deleted"
  )

  def getInsertValuesMap: Map[SQLSyntax, ParameterBinder] =
  {
    val table = User.column

    val insertMap = autoNamedValues[User](
      this,
      table,
      "user_id",
      "created",
      "updated",
      "soft_deleted"
    )
    //    val insertMap: Map[SQLSyntax, ParameterBinder] = Map(
    //      table.column("first_name") -> first_name,
    //      table.column("last_name") -> last_name,
    //      table.column("email") -> email,
    //      table.column("mobile_number") -> mobile_number,
    //      table.column("image") -> image,
    //      table.column("password") -> password,
    //      table.column("salt") -> salt,
    //      table.column("gender_id") -> gender_id,
    //      table.column("created") -> created,
    //      table.column("updated") -> updated
    //    )

    insertMap
  }

  def getUpdateValuesMap: Map[SQLSyntax, ParameterBinder] = _updateForm.updateValuesMap()

  def setFirstName(inFirstName: String): User =
  {
    _updateForm = _updateForm.copy(first_name = Some(inFirstName))
    this
  }

  def setLastName(inLastName: String): User =
  {
    _updateForm = _updateForm.copy(last_name = Some(inLastName))
    this
  }

  def setEmail(inEmail: String): User =
  {
    _updateForm = _updateForm.copy(email = Some(inEmail))
    this
  }

  def setMobileNumber(inMobileNumber: String): User =
  {
    _updateForm = _updateForm.copy(mobile_number = Some(inMobileNumber))
    this
  }

  def setImage(inImage: String): User =
  {
    _updateForm = _updateForm.copy(image = Some(inImage))
    this
  }

  def setPassword(inPassword: String): User =
  {
    _updateForm = _updateForm.copy(password = Some(inPassword))
    this
  }

  def setSalt(inSalt: String): User =
  {
    _updateForm = _updateForm.copy(salt = Some(inSalt))
    this
  }

  def setGenderId(inGenderId: String): User =
  {
    _updateForm = _updateForm.copy(gender_id = Some(inGenderId))
    this
  }

  def setUpdated(inUpdated: DateTime): User =
  {
    _updateForm = _updateForm.copy(updated = Some(inUpdated))
    this
  }

  def setSoftDeleted(inSoftDeleted: DateTime): User =
  {
    _updateForm = _updateForm.copy(soft_deleted = Some(inSoftDeleted))
    this
  }

  def syncOriginal(): User =
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
  first_name: Option[String] = None,
  last_name: Option[String] = None,
  email: Option[String] = None,
  mobile_number: Option[String] = None,
  image: Option[String] = None,
  password: Option[String] = None,
  salt: Option[String] = None,
  gender_id: Option[String] = None,
  updated: Option[DateTime] = None,
  soft_deleted: Option[DateTime] = None
)
{
  private val _nullValues: ListBuffer[String] = ListBuffer()

  def setNullValue(value: String): UserUpdateForm =
  {
    _nullValues.append(value)
    this
  }

  def setNullValue(values: Seq[String]): UserUpdateForm =
  {
    _nullValues.appendAll(values)
    this
  }

  def updateValuesMap(setNull: Seq[String] = _nullValues): Map[SQLSyntax, ParameterBinder] =
  {
    //    val column = User.column
    val updater = new ModelUpdater[User](User.column)
      .addIfPresent("first_name", first_name)
      .addIfPresent("last_name", last_name)
      .addIfPresent("email", email)
      .addIfPresent("mobile_number", mobile_number)
      .addIfPresent("image", image)
      .addIfPresent("password", password)
      .addIfPresent("salt", salt)
      .addIfPresent("gender_id", gender_id)
      .addIfPresent("updated", updated)
      .addIfPresent("soft_deleted", soft_deleted)

    //    var updateMap: Map[SQLSyntax, ParameterBinder] = Map.empty
    //
    //    updateMap = addIfPresent("first_name", first_name, updateMap, column)
    //    if (first_name.isDefined) updateMap = updateMap ++ Map(column.column("first_name") -> first_name.get)
    //
    //    if (last_name.isDefined) updateMap = updateMap ++ Map(column.column("last_name") -> last_name.get)
    //
    //    if (email.isDefined) updateMap = updateMap ++ Map(column.column("email") -> email.get)
    //
    //    if (mobile_number.isDefined) updateMap = updateMap ++ Map(column.column("mobile_number") -> mobile_number.get)
    //
    //    if (image.isDefined) updateMap = updateMap ++ Map(column.column("image") -> image.get)
    //
    //    if (password.isDefined) updateMap = updateMap ++ Map(column.column("password") -> password.get)
    //
    //    if (salt.isDefined) updateMap = updateMap ++ Map(column.column("salt") -> salt.get)
    //
    //    if (gender_id.isDefined) updateMap = updateMap ++ Map(column.column("gender_id") -> gender_id.get)
    //
    //    if (updated.isDefined) updateMap = updateMap ++ Map(column.column("updated") -> updated.get)
    //
    //    if (soft_deleted.isDefined) updateMap = updateMap ++ Map(column.column("soft_deleted") -> soft_deleted.get)

    //    updateMap
    updater.updateMap
  }
}

class ModelUpdater[M](
  column: scalikejdbc.ColumnName[M],
  private var _updateMap: Map[SQLSyntax, ParameterBinder] = Map.empty
)
{
  def addIfPresent[T](
    fieldName: String,
    value: Option[T]
  )(implicit binding: ParameterBinderFactory[T]): ModelUpdater[M] =
  {
    if (value.isDefined) _updateMap = _updateMap ++ Map(column.column(fieldName) -> value.get)
    this
  }

  def updateMap: Map[SQLSyntax, ParameterBinder] = _updateMap
}