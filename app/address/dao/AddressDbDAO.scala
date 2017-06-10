package address.dao

import javax.inject.{Inject, Singleton}

import address.models.{Address, AddressCompanion}
import com.nischal.base.BaseDbDAO
import com.nischal.exceptions.ModelNotFound

/**
  * Created by nbasnet on 6/9/17.
  */
@Singleton
class AddressDbDAO @Inject()(
  val addressCompanion: AddressCompanion
) extends BaseDbDAO[Address, Address, AddressCompanion] with IAddressDbDAO
{
  override val modelCompanion: AddressCompanion = addressCompanion

  override def modelFailMatch(optionModel: Option[Address], primaryId: String): Address = optionModel match {
    case Some(model: Address) => model
    case None => throw ModelNotFound(modelCompanion.tableName, modelCompanion.primaryKey, primaryId)
  }
}
