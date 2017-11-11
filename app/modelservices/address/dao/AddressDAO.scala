package modelservices.address.dao

import javax.inject.{Inject, Singleton}

import modelservices.address.models.Address
import scalikejdbc.DBSession

/**
  * Created by nbasnet on 6/9/17.
  */
@Singleton
class AddressDAO @Inject()(
  addressDbDAO: IAddressDbDAO
) extends IAddressDAO
{
  val defaultSession: DBSession = addressDbDAO.defaultSession

  def get(primaryId: String)(implicit session: DBSession = defaultSession): Option[Address] =
  {
    addressDbDAO.get(primaryId)
  }

  def getMany(primaryIds: Seq[String])(implicit session: DBSession = defaultSession): Seq[Address] =
  {
    addressDbDAO.getMany(primaryIds)
  }

  def getOrFail(primaryId: String)(implicit session: DBSession = defaultSession): Address =
  {
    addressDbDAO.getOrFail(primaryId)
  }

  def save(model: Address, primaryId: Option[String])(implicit session: DBSession = defaultSession): String =
  {
    addressDbDAO.save(model, primaryId)
  }

  def saveMany(model: Seq[Address], primaryId: Seq[String])(implicit session: DBSession = defaultSession): Seq[String] =
  {
    addressDbDAO.saveMany(model, primaryId)
  }
}
