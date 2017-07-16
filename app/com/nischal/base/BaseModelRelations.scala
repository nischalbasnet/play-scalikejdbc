package com.nischal.base

import com.nischal.db.ModelRelation

/**
  * Created by nbasnet on 6/9/17.
  */
abstract class BaseModelRelations extends Enumeration
{
  type BaseModelRelations = Value
  val ALL = Value
}

trait RelationDetail[FT, TT, JT]
{
  def name: String

  def relation: ModelRelation[FT, TT, JT]

  def returnJunctionTableInfo: Boolean
}

abstract class BaseModelRelationShips extends Enumeration
{

  /**
    * Inner class to hold the relation ship value for the enum value
    *
    * @param name
    * @param relation
    * @param returnJunctionTableInfo
    * @tparam FT
    * @tparam TT
    * @tparam JT
    */
  case class Val[FT, TT, JT](
    name: String,
    relation: ModelRelation[FT, TT, JT],
    returnJunctionTableInfo: Boolean = false
  ) extends super.Val with RelationDetail[FT, TT, JT]

}