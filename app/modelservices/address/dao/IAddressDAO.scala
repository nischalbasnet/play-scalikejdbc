package modelservices.address.dao

import modelservices.address.models.Address
import com.nischal.basecontracts.{IBaseReadDAO, IBaseWriteDAO}

/**
  * Created by nbasnet on 6/9/17.
  */
trait IAddressDAO extends IAddressReadDAO with IAddressWriteDAO

trait IAddressReadDAO extends IBaseReadDAO[Address, String]

trait IAddressWriteDAO extends IBaseWriteDAO[Address, String]

trait IAddressDbDAO extends IAddressReadDAO with IAddressWriteDAO