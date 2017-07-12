package modelservices.address.dao

import javax.inject.Singleton

import modelservices.address.models.Address
import com.nischal.base.BaseDbDAO
import com.nischal.exceptions.ModelNotFound
import services.events.ModelEvent

/**
  * Created by nbasnet on 6/9/17.
  */
@Singleton
class AddressDbDAO extends BaseDbDAO[Address] with IAddressDbDAO
{
  override val modelCompanion = Address
  override val modelEventBus: ModelEvent[Address] = new ModelEvent[Address]()

  override def modelFailMatch(optionModel: Option[Address], primaryId: String): Address = optionModel match {
    case Some(model: Address) => model
    case None => throw ModelNotFound(modelCompanion.tableName, modelCompanion.primaryKey, primaryId)
  }
}
