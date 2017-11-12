package modelservices.address.models

import java.time.LocalDateTime

import scalikejdbc.interpolation.SQLSyntax
import scalikejdbc.{ParameterBinder, autoNamedValues}

trait AddressATC
{
  self: Address =>

  protected var _updateForm: AddressUpdateForm = AddressUpdateForm()

  def getInsertValuesMap: Map[SQLSyntax, ParameterBinder] =
  {
    val insertMap = autoNamedValues[Address](
      this,
      Address.column,
      "address_id",
      "created",
      "updated",
      "soft_deleted"
    )
    //    val insertMap: Map[SQLSyntax, ParameterBinder] = Map(
    //      table.column("address_1") -> address_1,
    //      table.column("address_2") -> address_2,
    //      table.column("city") -> city,
    //      table.column("state_provience") -> state_provience,
    //      table.column("postal_code") -> postal_code,
    //      table.column("created") -> created,
    //      table.column("updated") -> updated,
    //      table.column("country") -> country
    //    )

    insertMap
  }

  def getUpdateValuesMap: Map[SQLSyntax, ParameterBinder] = _updateForm.updateValuesMap

  def setAddress1(inAddress1: String) =
  {
    _updateForm = _updateForm.copy(address_1 = Some(inAddress1))
    this
  }

  def setAddress2(inAddress2: String) =
  {
    _updateForm = _updateForm.copy(address_2 = Some(inAddress2))
    this
  }

  def setCity(inCity: String) =
  {
    _updateForm = _updateForm.copy(city = Some(inCity))
    this
  }

  def setStateProvience(inStateProvience: String) =
  {
    _updateForm = _updateForm.copy(state_provience = Some(inStateProvience))
    this
  }

  def setPostalCode(inPostalCode: Int) =
  {
    _updateForm = _updateForm.copy(postal_code = Some(inPostalCode))
    this
  }

  def setUpdated(inUpdated: LocalDateTime) =
  {
    _updateForm = _updateForm.copy(updated = Some(inUpdated))
    this
  }

  def setSoftDeleted(inSoftDeleted: LocalDateTime) =
  {
    _updateForm = _updateForm.copy(soft_deleted = Some(inSoftDeleted))
    this
  }

  def setCountry(inCountry: String) =
  {
    _updateForm = _updateForm.copy(country = Some(inCountry))
    this
  }

  def syncOriginal() =
  {
    this.copy(
      address_1 = _updateForm.address_1.getOrElse(address_1),
      address_2 = _updateForm.address_2 match {
        case Some(s: String) => Some(s)
        case _ => address_2
      },
      city = _updateForm.city match {
        case Some(s: String) => Some(s)
        case _ => city
      },
      state_provience = _updateForm.state_provience match {
        case Some(s: String) => Some(s)
        case _ => state_provience
      },
      postal_code = _updateForm.postal_code match {
        case Some(s: Int) => Some(s)
        case _ => postal_code
      },
      updated = _updateForm.updated.getOrElse(updated),
      soft_deleted = _updateForm.soft_deleted match {
        case Some(s: LocalDateTime) => Some(s)
        case _ => soft_deleted
      },
      country = _updateForm.country.getOrElse(country)
    )
  }

}

/**
  * Addresses companion Object
  */
case class AddressUpdateForm(
  address_1: Option[String] = None,
  address_2: Option[String] = None,
  city: Option[String] = None,
  state_provience: Option[String] = None,
  postal_code: Option[Int] = None,
  updated: Option[LocalDateTime] = None,
  soft_deleted: Option[LocalDateTime] = None,
  country: Option[String] = None
)
{
  def updateValuesMap: Map[SQLSyntax, ParameterBinder] =
  {
    val table = Address.column
    var updateMap: Map[SQLSyntax, ParameterBinder] = Map.empty

    if (address_1.isDefined) updateMap = updateMap ++ Map(table.column("address_1") -> address_1.get)

    if (address_2.isDefined) updateMap = updateMap ++ Map(table.column("address_2") -> address_2.get)

    if (city.isDefined) updateMap = updateMap ++ Map(table.column("city") -> city.get)

    if (state_provience.isDefined) updateMap = updateMap ++ Map(table.column("state_provience") -> state_provience.get)

    if (postal_code.isDefined) updateMap = updateMap ++ Map(table.column("postal_code") -> postal_code.get)

    if (updated.isDefined) updateMap = updateMap ++ Map(table.column("updated") -> updated.get)

    if (soft_deleted.isDefined) updateMap = updateMap ++ Map(table.column("soft_deleted") -> soft_deleted.get)

    if (country.isDefined) updateMap = updateMap ++ Map(table.column("country") -> country.get)

    updateMap
  }
}

