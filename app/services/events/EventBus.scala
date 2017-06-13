package services.events

import com.nischal.base.{BaseModel, BaseModelCompanion}
import rx.lang.scala.{Observable, Subject}
import rx.lang.scala.subjects.{PublishSubject, SerializedSubject}
import scalikejdbc.{ParameterBinder, SQLSyntax}
import services.events.ModelEvents.ModelEvents

/**
  * Created by nbasnet on 6/12/17.
  */
/**
  * Trait that defines the event bus for type T
  *
  * @tparam T
  */
trait EventBus[T]
{
  private val _bus: Subject[T] = SerializedSubject[T](PublishSubject.apply())

  /**
    * Send event
    *
    * @param event
    *
    * @return
    */
  def sendEvent(event: T): EventBus[T] =
  {
    _bus.onNext(event)
    this
  }

  /**
    * Get observable from the bus
    *
    * @return
    */
  def toObservable: Observable[T] = _bus
}

/**
  * Event for models
  *
  * @tparam M
  */
class ModelEvent[M <: BaseModel[M]] extends EventBus[ModelEventPayload[M]]

/**
  * Types of model events
  */
object ModelEvents extends Enumeration
{
  type ModelEvents = Value

  val CREATED, UPDATED = Value
}

/**
  * Payload that is sent for model events
  *
  * @param data
  * @param modelCompanion
  * @param event
  * @tparam M
  */
case class ModelEventPayload[M <: BaseModel[M]](
  data: Map[SQLSyntax, ParameterBinder],
  modelCompanion: BaseModelCompanion[M],
  event: ModelEvents
)

/**
  * Trait defining observer for models events
  *
  * @tparam M
  */
trait IObserveModelEvent[M <: BaseModel[M]]
{
  def created(payload: ModelEventPayload[M])

  def update(payload: ModelEventPayload[M])
}