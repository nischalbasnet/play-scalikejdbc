package com.nischal.base

import com.nischal.basecontracts.IBaseDAO
import scalikejdbc._
import services.events.{IObserveModelEvent, ModelEvent, ModelEventPayload, ModelEvents}

/**
  * Created by nbasnet on 6/4/17.
  */
abstract class BaseDbDAO[MT <: BaseModel[MT]] extends IBaseDAO[MT, String]
{
  def modelCompanion: BaseModelCompanion[MT]

  def modelEventBus: ModelEvent[MT]

  def modelObserver: Option[IObserveModelEvent[MT]] = None

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

  //TODO FIX the return of this function
  /**
    *
    * @param model
    * @param primaryId
    * @param session
    *
    * @return
    */
  def save(model: MT, primaryId: Option[String])(implicit session: DBSession = AutoSession): String =
  {
    primaryId match {
      case Some(pId: String) => {
        performModelUpdate(model, pId).toString
      }
      case None => performModelInsert(model)
    }
  }

  /**
    *
    * @param model
    * @param primaryId
    * @param session
    *
    * @return
    */
  def saveMany(model: Seq[MT], primaryId: Seq[String])(implicit session: DBSession = AutoSession): Seq[String] =
  {
    primaryId match {
      case Nil => performModelBatchInsert(model)
      case m: Seq[MT] => performModelBatchUpdate(model, primaryId)
    }
  }

  /**
    *
    * @param model
    * @param primaryId
    * @param session
    *
    * @return
    */
  def performModelUpdate(model: MT, primaryId: String)(implicit session: DBSession = AutoSession): Int =
  {
    val updateValues: Map[SQLSyntax, ParameterBinder] = model.updateValuesMap

    performUpdate(primaryId, updateValues)
  }

  /**
    *
    * @param primaryId
    * @param updateValues
    * @param session
    *
    * @return
    */
  def performUpdate(
    primaryId: String,
    updateValues: Map[SQLSyntax, ParameterBinder]
  )(implicit session: DBSession): Int =
  {
    val success = applyUpdate {
      update(modelCompanion).set(
        updateValues
      ).where.eq(modelCompanion.column.column(modelCompanion.primaryKey), primaryId)
    }

    if (success != 0) {
      if(modelObserver.isDefined){
        modelEventBus.toObservable.subscribe(p => modelObserver.get.update(p))
      }
      modelEventBus.sendEvent(
        ModelEventPayload(updateValues, modelCompanion, ModelEvents.UPDATED)
      )
    }

    success
  }

  /**
    *
    * @param model
    * @param primaryId
    * @param session
    *
    * @return
    */
  def performModelBatchUpdate(model: Seq[MT], primaryId: Seq[String])(implicit session: DBSession = AutoSession): Seq[String] =
  {
    throw new NotImplementedError("batch update not implemented")
  }

  /**
    *
    * @param model
    * @param session
    *
    * @return
    */
  def performModelInsert(model: MT)(implicit session: DBSession = AutoSession): String =
  {
    performInsertAndReturnId(model.insertValuesMap)
  }

  /**
    * perfrom insert operation and return id
    *
    * @param insertValues
    * @param session
    *
    * @return
    */
  def performInsertAndReturnId(
    insertValues: Map[SQLSyntax, ParameterBinder]
  )(implicit session: DBSession): String =
  {
    //Required to use SQLToResult class to insert value
    import scalikejdbc.com.nischal.db.SqlHelpers._

    val returnedId = withSQL {
      insert.into(modelCompanion).namedValues(
        insertValues
      ).returning(modelCompanion.column.column(modelCompanion.primaryKey))
    }
      //now get the primary key returned from insert operation
      .map(_.string(modelCompanion.primaryKey))
      .first()
      //true here allows us to use append apply method that can perform insert update operation
      .apply(true)
      .getOrElse("")

    if (returnedId.nonEmpty) {
      if(modelObserver.isDefined){
        modelEventBus.toObservable.subscribe(p => modelObserver.get.created(p))
      }

      modelEventBus.sendEvent(
        ModelEventPayload(insertValues, modelCompanion, ModelEvents.CREATED)
      )
    }

    returnedId
  }

  /**
    *
    * @param models
    * @param session
    *
    * @return
    */
  def performModelBatchInsert(models: Seq[MT])(implicit session: DBSession = AutoSession): Seq[String] =
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
    case Some(a: String) => sqls" AND ${a} NOTNULL "
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
