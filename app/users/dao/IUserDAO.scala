package users.dao

import com.nischal.basecontracts.{IBaseReadDAO, IBaseWriteDAO}
import users.models.User

/**
  * Created by nbasnet on 6/4/17.
  */
trait IUserDAO extends IUserReadDAO with IUserWriteDAO

trait IUserReadDAO extends IBaseReadDAO[User, String]

trait IUserWriteDAO extends IBaseWriteDAO[User, String]

trait IUserPostgresDAO extends IUserReadDAO with IUserWriteDAO