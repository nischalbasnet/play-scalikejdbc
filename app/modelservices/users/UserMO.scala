package modelservices.users

import play.api.Logger
import services.events.{IObserveModelEvent, ModelEventPayload}
import modelservices.users.models.User

/**
  * Created by nbasnet on 6/12/17.
  */
/**
  * Observer for modelservices.users model events
  */
class UserMO extends IObserveModelEvent[User]
{
  override def created(payload: ModelEventPayload[User]): Unit =
  {
    Logger.info("User MO call for created")
    Logger.info(s"user is create with values ${payload.data.toString()}")
  }

  override def update(payload: ModelEventPayload[User]): Unit =
  {
    Logger.info("User MO call for updated")
    Logger.info(s"user is updated with values ${payload.data.toString()}")
  }
}

object UserMO
{
  def apply(): UserMO = new UserMO()
}