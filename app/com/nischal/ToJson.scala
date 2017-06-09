package com.nischal

import play.api.libs.json.{JsValue, Json, Reads, Writes}

/**
  * Created by nbasnet on 6/5/17.
  */
trait ToJson[T]
{
  self: T =>

  def toJson()(implicit writer: Writes[T]): JsValue = Json.toJson(this)

  def fromJson(input: String)(implicit reader: Reads[T]): Option[T] = Json.parse(input).asOpt[T]
}