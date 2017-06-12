package com.nischal.base

import play.api.libs.json.{JsValue, Json, Writes}
import scalikejdbc._

/**
  * Created by nbasnet on 6/4/17.
  */
abstract class BaseModelCompanion[MT] extends SQLSyntaxSupport[MT]
{
  type SQLSyntaxT[MT] = scalikejdbc.QuerySQLSyntaxProvider[scalikejdbc.SQLSyntaxSupport[MT], MT]

  def defaultTable: SQLSyntaxT[MT]

  def primaryKey: String

  def archivedField: Option[String]

  def fromSqlResult(rn: ResultName[MT])(rs: WrappedResultSet): MT

  def toJson(m: Seq[MT])(implicit write: Writes[MT]): JsValue = Json.toJson(m)(Writes.traversableWrites(write))
}

object SQLSyntaxType
{
  type SQLSyntaxT[MT] = scalikejdbc.QuerySQLSyntaxProvider[scalikejdbc.SQLSyntaxSupport[MT], MT]
}