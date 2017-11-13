package com.nischal.base

import com.nischal.ToJson
import play.api.libs.json.{JsValue, Writes}
import scalikejdbc.ParameterBinder
import scalikejdbc.interpolation.SQLSyntax

/**
  * Created by nbasnet on 6/4/17.
  */
abstract class BaseModel[T] extends ToJson[T]
{
  self: T =>

  def getInsertValuesMap: Map[SQLSyntax, ParameterBinder]

  def getUpdateValuesMap: Map[SQLSyntax, ParameterBinder]

  def setRelation[A](relation: BaseModel[A]) = ???
}

trait BaseEntity[MT <: BaseModel[MT]]
{
  def data: MT

  def setRelation[RT](relation: Seq[RT]): Unit

  def toJson(implicit writes: Writes[MT]): JsValue = data.toJson()
}

trait BaseEntityCompanion[ET, MT, DAO]
{
  def apply(m: MT, dao: DAO): ET
}