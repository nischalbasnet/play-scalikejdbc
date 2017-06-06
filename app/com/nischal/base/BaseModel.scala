package com.nischal.base

import scalikejdbc.ParameterBinder
import scalikejdbc.interpolation.SQLSyntax

/**
  * Created by nbasnet on 6/4/17.
  */
abstract class BaseModel
{
  def insertValuesMap: Map[SQLSyntax, ParameterBinder]

  def updateValuesMap: Map[SQLSyntax, ParameterBinder]
}
