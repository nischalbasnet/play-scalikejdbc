package com.nischal.base

import com.nischal.ToJson
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
