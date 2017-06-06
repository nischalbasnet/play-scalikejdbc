package com.nischal.base

import com.nischal.basecontracts.IBaseDAO
import scalikejdbc._

/**
  * Created by nbasnet on 6/4/17.
  */
abstract class BaseDAO[MT <: BaseModel, MC <: BaseModelCompanion[MT]] extends IBaseDAO[MT, String]
{
  def modelCompanion: MC

  def modelFailMatch(optionModel: Option[MT], primaryId: String): MT

  /**
    *
    * @param primaryId
    *
    * @return
    */
  def get(primaryId: String)(implicit session: DBSession = AutoSession): Option[MT] =
  {
    val table = modelCompanion.syntax("tt")

    val query = queryGet(primaryId)
      .map(modelCompanion.fromSqlResult(table.resultName)(_))
      .single()
      .apply()

    query
  }

  /**
    *
    * @param primaryId
    *
    * @return
    */
  def getOrFail(primaryId: String)(implicit session: DBSession = AutoSession): MT =
  {
    modelFailMatch(get(primaryId), primaryId)
  }

  /**
    * Get Many
    *
    * @param primaryIds
    *
    * @return
    */
  def getMany(primaryIds: Seq[String])(implicit session: DBSession = AutoSession): Seq[MT] =
  {
    val table = modelCompanion.syntax("tt")

    val query = queryGetMany(primaryIds)
      .map(modelCompanion.fromSqlResult(table.resultName)(_))
      .collection
      .apply()

    query
  }

  def save(model: MT, primaryId: Option[String])(implicit session: DBSession = AutoSession): String =
  {
    primaryId match {
      case Some(pId: String) => performUpdate(model, pId)
      case None => performInsert(model)
    }
  }

  def saveMany(model: Seq[MT], primaryId: Seq[String])(implicit session: DBSession = AutoSession): Seq[String] =
  {
    primaryId match {
      case Nil => performBatchInsert(model)
      case m: Seq[MT] => performBatchUpdate(model, primaryId)
    }
  }

  def performUpdate(model: MT, primaryId: String)(implicit session: DBSession = AutoSession): String =
  {
    val updateValues = model.updateValuesMap ++ Map(modelCompanion.column.column(modelCompanion.primaryKey) -> primaryId)

    applyUpdateAndReturnGeneratedKey {
      insert.into(modelCompanion).namedValues(
        updateValues
      )
    }.asInstanceOf[String]
  }

  def performBatchUpdate(model: Seq[MT], primaryId: Seq[String])(implicit session: DBSession = AutoSession): Seq[String] =
  {
    throw new NotImplementedError("batch update not implemented")
  }

  def performInsert(model: MT)(implicit session: DBSession = AutoSession): String =
  {
    //TO DO fix this
    applyUpdateAndReturnGeneratedKey {
      insert.into(modelCompanion).namedValues(
        model.insertValuesMap
      )
    }.asInstanceOf[String]
  }

  def performBatchInsert(models: Seq[MT])(implicit session: DBSession = AutoSession): Seq[String] =
  {
    val insertFields: Seq[(SQLSyntax, ParameterBinder)] = models.head.insertValuesMap.map(_._1 -> sqls.?).toSeq
    val batchInsertValues = models.map(_.insertValuesMap.map(_._2).toSeq)

    withSQL {
      insert.into(modelCompanion).namedValues(insertFields: _*)
    }.batch(batchInsertValues: _*).apply().asInstanceOf[Seq[String]]
  }

  /** ##=============================##=============================##=============================
    * QUERY METHODS
    * ##=============================##=============================##=============================
    */

  /**
    * Modelds archive Filter
    *
    * @return
    */
  def queryArchiveFilter = modelCompanion.archivedField match {
    case Some(a: String) => sqls" AND ${a} NOT NULL "
    case _ => sqls""
  }

  /**
    * Get Query
    *
    * @param primaryId
    *
    * @return
    */
  def queryGet(primaryId: String) =
  {
    val table = modelCompanion.syntax("tt")

    sql"""
           SELECT ${table.result.*}
           FROM ${modelCompanion.as(table)}
           WHERE ${table.column(modelCompanion.primaryKey)} = ${primaryId}
            ${queryArchiveFilter}
         """
  }

  def queryGetMany(primaryIds: Seq[String]) =
  {
    val table = modelCompanion.syntax("tt")

    sql"""
           SELECT ${table.result.*}
           FROM ${modelCompanion.as(table)}
           WHERE ${table.column(modelCompanion.primaryKey)} IN (${primaryIds})
            ${queryArchiveFilter}
         """
  }

}
