package com.nischal.base

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
}
