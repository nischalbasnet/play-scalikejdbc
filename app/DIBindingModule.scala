import com.google.inject.AbstractModule
import users.dao.{IUserDAO, IUserPostgresDAO, UserDAO, UserPostgresDAO}

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
    bind(classOf[IUserPostgresDAO]).to(classOf[UserPostgresDAO])
    //main DAO binding
    bind(classOf[IUserDAO]).to(classOf[UserDAO])
  }
}
