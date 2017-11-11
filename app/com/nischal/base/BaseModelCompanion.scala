package com.nischal.base

import play.api.libs.json.{JsValue, Json, Reads, Writes}
import scalikejdbc._

/**
  * Created by nbasnet on 6/4/17.
  */
abstract class BaseModelCompanion[MT] extends SQLSyntaxSupport[MT]
{
  type SQLSyntaxT[MO] = scalikejdbc.QuerySQLSyntaxProvider[scalikejdbc.SQLSyntaxSupport[MO], MO]
  type Model = MT

  implicit def reads: Reads[Model]

  def defaultTable: SQLSyntaxT[MT]

  def primaryKey: String

  def archivedField: Option[String]

  def fromSqlResult(rs: WrappedResultSet, rn: ResultName[MT]): MT

  def toJson(m: Seq[MT])(implicit write: Writes[MT]): JsValue = Json.toJson(m)(Writes.traversableWrites(write))

  def setModelRelation[A](model: Model, relation: Seq[A]): Unit = ???
}

object SQLSyntaxType
{
  type SQLSyntaxT[MT] = scalikejdbc.QuerySQLSyntaxProvider[scalikejdbc.SQLSyntaxSupport[MT], MT]
}