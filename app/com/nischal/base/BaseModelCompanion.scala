package com.nischal.base

import scalikejdbc._

/**
  * Created by nbasnet on 6/4/17.
  */
abstract class BaseModelCompanion[MT] extends SQLSyntaxSupport[MT]
{
  def primaryKey: String

  def archivedField: Option[String]

  /**
    * If empty all fields except primary id is used when inserting
    *
    * @return
    */
  def insertableFields: Seq[String] = Seq.empty

  def fromSqlResult(u: ResultName[MT])(rs: WrappedResultSet): MT
}
