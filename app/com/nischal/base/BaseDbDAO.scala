package com.nischal.base

import com.nischal.basecontracts.IBaseDAO
import com.nischal.exceptions.ModelNotFound
import play.api.libs.json.{Json, Reads}
import scalikejdbc._
import services.events.{IObserveModelEvent, ModelEvent, ModelEventPayload, ModelEvents}

/**
  * Created by nbasnet on 6/4/17.
  */
abstract class BaseDbDAO[MT <: BaseModel[MT]] extends IBaseDAO[MT, String]
{
  val defaultSession: DBSession = AutoSession

  def modelCompanion: BaseModelCompanion[MT]

  def modelEventBus: ModelEvent[MT]

  def modelObserver: Option[IObserveModelEvent[MT]] = None

  /** s
    *
    * @param primaryId
    * @return
    */
  def get(primaryId: String)(implicit session: DBSession = defaultSession): Option[MT] =
  {
    val query = queryGet(primaryId)
      .map(modelCompanion.fromSqlResult)
      .single()
      .apply()

    query
  }

  /**
    * Get model with the given relations
    *
    * @param primaryId
    * @param relations
    * @param session
    * @return
    */
  def getWith(primaryId: String, relations: Seq[RelationDetail[_, _, _]])(implicit session: DBSession = defaultSession): Option[MT] =
  {
    val fullQuery = queryGetWithRelations(primaryId, relations)

    val modelInfo = fullQuery.map(rs => {
      val mdl: MT = modelCompanion.fromSqlResult(rs)

      //set relation value from result set
      relations.foreach(r => {
        val tpe = r.relation.toTable
        val relationValueSet = Json.parse(rs.string(r.name)).as[Set[tpe.Model]](Reads.set(tpe.reads))

        modelCompanion.setModelRelation(mdl, relationValueSet.toSeq)
      })

      mdl
    }).first().apply()

    modelInfo
  }

  /**
    * Query to get the model along with the given relations
    *
    * @param primaryId
    * @param relations
    * @param session
    * @return
    */
  private def queryGetWithRelations(
    primaryId: String,
    relations: Seq[RelationDetail[_, _, _]]
  )(implicit session: DBSession = defaultSession): SQL[Nothing, NoExtractor] =
  {
    val table = modelCompanion.defaultTable

    var selectFields = sqls"SELECT ${table.resultAll}"
    var relationJoins = sqls""
    val modelFilter =
      sqls"""
            WHERE ${table.column(modelCompanion.primaryKey)} = $primaryId
            $queryArchiveFilter
            GROUP BY ${table.column(modelCompanion.primaryKey)}
          """

    var relationCount = 0
    //load the relations
    relations.foreach { r =>
      import scalikejdbc.nischalmod.SqlHelpers.createSqlSyntax
      val subQueryAlias = s"${r.relation.fromTableKey.head}_$relationCount${r.relation.toTableKey.head}"
      //add to select field
      selectFields = selectFields.append(
        createSqlSyntax(s",json_agg(row_to_json($subQueryAlias.*)) as ${r.name} ")
      )

      relationJoins = relationJoins.append(
        sqls"""${
          r.relation.getJoinSubQuery(
            "left",
            subQueryAlias,
            primaryId,
            aliasedResultName = false,
            returnJunctionTableInfo = r.returnJunctionTableInfo
          )
        }"""
          .append(
            sqls""" ON TRUE """
          )
      )
      relationCount += 1
    }

    val fullQuery =
      sql"""
           $selectFields
            FROM ${modelCompanion.as(table)}
              $relationJoins
            $modelFilter
         """

    fullQuery
  }

  /**
    * Get the specified relation
    *
    * @param linkId
    * @param relationDetail
    * @param relationCompanion
    * @param session
    * @tparam R
    * @return
    */
  def getRelation[R, JT](
    linkId: String,
    relationDetail: RelationDetail[MT, R, JT],
    relationCompanion: BaseModelCompanion[R]
  )(implicit session: DBSession = defaultSession): List[R] =
  {
    val toTable = relationCompanion.syntax("ft")
    val query = relationDetail.relation.getQuery(
      linkId,
      toSyntax = Some(toTable)
    )

    withSQL {
      query
    }.map(relationCompanion.fromSqlResult(_, toTable.resultName))
      .toList()
      .apply()
  }

  /**
    *
    * @param primaryId
    * @return
    */
  def getOrFail(primaryId: String)(implicit session: DBSession = defaultSession): MT =
  {
    val modelObject = get(primaryId)

    if (modelObject != null && modelObject.isDefined) {
      modelObject.get
    }
    else {
      throw ModelNotFound(modelCompanion.tableName, modelCompanion.primaryKey, primaryId)
    }
  }

  /**
    * Get Many
    *
    * @param primaryIds
    * @return
    */
  def getMany(primaryIds: Seq[String])(implicit session: DBSession = defaultSession): Seq[MT] =
  {
    val query = queryGetMany(primaryIds)
      .map(modelCompanion.fromSqlResult)
      .list()
      .apply()

    query
  }

  //TODO FIX the return of this function
  /**
    *
    * @param model
    * @param primaryId
    * @param session
    * @return
    */
  def save(model: MT, primaryId: Option[String])(implicit session: DBSession): String =
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
    * @return
    */
  def saveMany(model: Seq[MT], primaryId: Seq[String])(implicit session: DBSession = defaultSession): Seq[String] =
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
    * @return
    */
  def performModelUpdate(model: MT, primaryId: String)(implicit session: DBSession = defaultSession): Int =
  {
    val updateValues: Map[SQLSyntax, ParameterBinder] = model.getUpdateValuesMap

    performUpdate(primaryId, updateValues)
  }

  /**
    *
    * @param primaryId
    * @param updateValues
    * @param session
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
      if (modelObserver.isDefined) {
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
    * @return
    */
  def performModelBatchUpdate(model: Seq[MT], primaryId: Seq[String])(implicit session: DBSession = defaultSession): Seq[String] =
  {
    throw new NotImplementedError("batch update not implemented")
  }

  /**
    *
    * @param model
    * @param session
    * @return
    */
  def performModelInsert(model: MT)(implicit session: DBSession = defaultSession): String =
  {
    performInsertAndReturnId(model.getInsertValuesMap)
  }

  /**
    * perfrom insert operation and return id
    *
    * @param insertValues
    * @param session
    * @return
    */
  def performInsertAndReturnId(
    insertValues: Map[SQLSyntax, ParameterBinder]
  )(implicit session: DBSession = defaultSession): String =
  {
    //Required to use SQLToResult class to insert value
    import scalikejdbc.nischalmod.SqlHelpers._

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
      if (modelObserver.isDefined) {
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
    * @return
    */
  def performModelBatchInsert(models: Seq[MT])(implicit session: DBSession = defaultSession): Seq[String] =
  {
    val insertFields: Seq[(SQLSyntax, ParameterBinder)] = models.head.getInsertValuesMap.map(_._1 -> sqls.?).toSeq
    val batchInsertValues = models.map(_.getInsertValuesMap.values.toSeq)

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
  def queryArchiveFilter: SQLSyntax = modelCompanion.archivedField match {
    case Some(a: String) => sqls" AND ${a} NOTNULL "
    case _ => sqls""
  }

  /**
    * Get Query
    *
    * @param primaryId
    * @return
    */
  def queryGet(primaryId: String) =
  {
    val table = modelCompanion.defaultTable

    sql"""
           SELECT ${table.result.*}
           FROM ${modelCompanion.as(table)}
           WHERE ${table.column(modelCompanion.primaryKey)} = ${primaryId}
            ${queryArchiveFilter}
         """
  }

  def queryGetMany(primaryIds: Seq[String]) =
  {
    val table = modelCompanion.defaultTable

    sql"""
           SELECT ${table.result.*}
           FROM ${modelCompanion.as(table)}
           WHERE ${table.column(modelCompanion.primaryKey)} IN (${primaryIds})
            ${queryArchiveFilter}
         """
  }
}

trait DbDAOWithEntity[MT <: BaseModel[MT], ET <: BaseEntity[MT]]
{
  self: BaseDbDAO[MT] =>

  def entityCompanion: BaseEntityCompanion[ET, MT, self.type]

  /**
    * Get user entity
    *
    * @param primaryId
    * @param session
    * @return
    */
  def getEntity(primaryId: String)(implicit session: DBSession = defaultSession): Option[ET] =
  {
    get(primaryId) match {
      case Some(m) => Some(entityCompanion(m, this))
      case _ => None
    }
  }

  /**
    * Get user entity or fail with exception
    *
    * @param primaryId
    * @param session
    * @return
    */
  def getEntityOrFail(primaryId: String)(implicit session: DBSession = defaultSession): ET =
  {
    entityCompanion(getOrFail(primaryId), this)
  }
}
