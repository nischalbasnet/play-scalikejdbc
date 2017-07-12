import modelservices.address.{AddressService, IAddressService}
import modelservices.address.dao.{AddressDAO, AddressDbDAO, IAddressDAO, IAddressDbDAO}
import com.google.inject.AbstractModule
import modelservices.users.{IUserService, UserService}
import modelservices.users.dao.{IUserDAO, IUserDbDAO, UserDAO, UserDbDAO}

/**
  * Created by nbasnet on 6/5/17.
  */
class DIBindingModule extends AbstractModule
{
  override def configure(): Unit =
  {
    /**
      * BINDINGS FOR DAO'S
      */
    bindDAOs()
  }

  private def bindDAOs(): Unit =
  {
    /**
      * USERS DAO's
      */
    //postgresDAO binding
    bind(classOf[IUserDbDAO]).to(classOf[UserDbDAO])
    //main DAO binding
    bind(classOf[IUserDAO]).to(classOf[UserDAO])
    //service binding
    bind(classOf[IUserService]).to(classOf[UserService])

    /**
      * Address DAO's
      */
    //postgresDAO binding
    bind(classOf[IAddressDbDAO]).to(classOf[AddressDbDAO])
    //main DAO binding
    bind(classOf[IAddressDAO]).to(classOf[AddressDAO])
    //service binding
    bind(classOf[IAddressService]).to(classOf[AddressService])
  }
}
