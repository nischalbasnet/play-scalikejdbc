package com.nischal.base

import com.nischal.base.Success.Success
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.Controller

import scala.xml.{Elem, NodeBuffer}

/**
  * Created by nbasnet on 6/4/17.
  */
abstract class BaseController extends Controller

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
    *
    * @return
    */
  def jsonOk(data: JsValue = Json.obj(), message: String = "", errorCode: String = ""): JsObject =
  {
    json(data, message, errorCode, Success.ok)
  }

  /**
    * failed json response
    *
    * @param data
    * @param message
    * @param errorCode
    *
    * @return
    */
  def jsonFail(data: JsValue = Json.obj(), message: String = "", errorCode: String = ""): JsObject =
  {
    json(data, message, errorCode, Success.fail)
  }

  /**
    * json response
    *
    * @param data
    * @param message
    * @param errorCode
    * @param success
    *
    * @return
    */
  def json(data: JsValue = Json.obj(), message: String = "", errorCode: String = "", success: Success = Success.ok): JsObject =
  {
    Json.obj(
      "data" -> data,
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
    *
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
    *
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
    *
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