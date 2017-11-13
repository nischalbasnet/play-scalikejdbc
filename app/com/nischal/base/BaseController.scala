package com.nischal.base

import com.nischal.base.Success.Success
import play.api.data.Form
import play.api.libs.json.{JsObject, JsValue, Json, Writes}
import play.api.mvc._

import scala.xml.Elem

/**
  * Created by nbasnet on 6/4/17.
  */
abstract class BaseController(cc: ControllerComponents) extends AbstractController(cc)
{
  val fakeId = "fake_id_used_for_creating_new_record_12345678910"

  def handleRequestError[T](error: Form[T], data: JsValue = Json.obj()): JsObject = NormalizedResponse.jsonFail(
    data = Some(data),
    message = error.errors.map(e => s"${e.key} = ${e.message}").mkString(""),
    errorCode = "REQUEST_VALIDATION"
  )
}

/**
  * Output normalizer
  */
object NormalizedResponse
{
  def currentDateTime = org.joda.time.DateTime.now().toString("MM/dd/yyyy HH:mm:ss")

  /**
    * successful json response
    *
    * @param data
    * @param message
    * @param errorCode
    * @return
    */
  //  def jsonOk(data: JsValue = Json.obj(), message: String = "", errorCode: String = ""): JsObject =
  //  {
  //    json(data, message, errorCode, Success.ok)
  //  }

  /**
    * failed json response
    *
    * @param data
    * @param message
    * @param errorCode
    * @return
    */
  //  def jsonFail(data: JsValue = Json.obj(), message: String = "", errorCode: String = ""): JsObject =
  //  {
  //    json(data, message, errorCode, Success.fail)
  //  }

  /**
    * json response
    *
    * @param data
    * @param message
    * @param errorCode
    * @param success
    * @return
    */
  //  def json(data: JsValue = Json.obj(), message: String = "", errorCode: String = "", success: Success = Success.ok): JsObject =
  //  {
  //    Json.obj(
  //      "data" -> data,
  //      "message" -> message,
  //      "success" -> success.id,
  //      "errorCode" -> errorCode,
  //      "timeStamp" -> currentDateTime
  //    )
  //  }
  def jsonFail(): JsObject = jsonFail(Json.obj(), "", "")

  def jsonFail(message: String): JsObject = jsonFail(Json.obj(), message, "")

  def jsonFail(message: String, errorCode: String): JsObject = jsonFail(Json.obj(), message, errorCode)

  def jsonFail[T](data: T, message: String, errorCode: String)(implicit writes: Writes[T]): JsObject =
  {
    json(data, message, "", Success.fail)
  }

  def jsonOk(): JsObject = jsonOk("")

  def jsonOk(message: String): JsObject = jsonOk(Json.obj(), message)

  def jsonOk[T](data: T)(implicit writes: Writes[T]): JsObject = json(data, "", "", Success.ok)

  def jsonOk[T](data: T, message: String)(implicit writes: Writes[T]): JsObject = json(data, message, "", Success.ok)

  def json[T](data: T, message: String, errorCode: String, success: Success)(implicit writes: Writes[T]): JsObject =
  {
    Json.obj(
      "data" -> Json.toJson(data),
      "message" -> message,
      "success" -> success.id,
      "errorCode" -> errorCode,
      "timeStamp" -> currentDateTime
    )
  }

  /**
    * successful xml
    *
    * @param data
    * @param message
    * @param errorCode
    * @return
    */
  def xmlOk(data: Option[Elem] = None, message: String = "", errorCode: String = "") =
  {
    xml(data, message, errorCode, Success.ok)
  }

  /**
    * failed xml
    *
    * @param data
    * @param message
    * @param errorCode
    * @return
    */
  def xmlFail(data: Option[Elem] = None, message: String = "", errorCode: String = "") =
  {
    xml(data, message, errorCode, Success.fail)
  }

  /**
    * xml output
    *
    * @param data
    * @param message
    * @param errorCode
    * @param success
    * @return
    */
  def xml(data: Option[Elem] = None, message: String = "", errorCode: String = "", success: Success = Success.ok): Elem =
  {
    <Response>
      <data>
        {data}
      </data>
      <message>
        {message}
      </message>
      <errorCode>
        {errorCode}
      </errorCode>
      <success>
        {success.id}
      </success>
      <timeStamp>
        {currentDateTime}
      </timeStamp>
    </Response>
  }
}

/**
  * Success enum
  */
object Success extends Enumeration
{
  type Success = Value
  val ok = Value(1)
  val fail = Value(0)
}

/**
  * ResponseFormat enum
  */
object ResponseFormat extends Enumeration
{
  type ResponseFormat = Value
  val JSON, XML = Value
}