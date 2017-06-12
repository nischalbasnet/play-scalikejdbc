package com.nischal.db

import com.nischal.base.BaseModelCompanion
import com.nischal.base.SQLSyntaxType.SQLSyntaxT
import com.nischal.db.RelationTypes.RelationTypes
import scalikejdbc._

/**
  * Created by nbasnet on 6/11/17.
  */
case class ModelRelation[FT, TT, JT](
  relationType: RelationTypes,
  fromTable: BaseModelCompanion[FT],
  fromTableKey: String,
  toTable: BaseModelCompanion[TT],
  toTableKey: String,
  junctionTable: Option[BaseModelCompanion[JT]] = None,
  junctionFromTableKey: Option[String] = None,
  junctionToTableKey: Option[String] = None
)
{
  /**
    * Variable to hold relation query
    */
  private var _overriddenQuery: Option[scalikejdbc.SQLBuilder[TT]] = None

  /**
    * set overridden query
    *
    * @param overriddenQuery
    *
    * @return
    */
  def setOverriddenQuery(overriddenQuery: scalikejdbc.SQLBuilder[TT]): ModelRelation[FT, TT, JT] =
  {
    _overriddenQuery = Some(overriddenQuery)
    this
  }

  def defaultMapper(rs: WrappedResultSet): TT =
  {
    toTable.fromSqlResult(toTable.defaultTable.resultName)(rs)
  }

  /**
    * Get the query
    *
    * @param linkKey
    * @param fromSyntax
    * @param toSyntax
    * @param junctionSyntax
    * @param aliasedResultName
    * @param returnJunctionTableInfo
    *
    * @return
    */
  def getQuery(
    linkKey: String,
    fromSyntax: Option[SQLSyntaxT[FT]] = None,
    toSyntax: Option[SQLSyntaxT[TT]] = None,
    junctionSyntax: Option[SQLSyntaxT[JT]] = None,
    aliasedResultName: Boolean = true,
    returnJunctionTableInfo: Boolean = false,
    getSimpleQuery: Boolean = true
  ): scalikejdbc.SQLBuilder[TT] =
  {
    if (_overriddenQuery.isDefined) {
      _overriddenQuery.get
    }
    else if (junctionTable.isDefined) {
      defaultJunctionQuery(
        linkKey,
        fromSyntax,
        toSyntax,
        junctionSyntax,
        aliasedResultName,
        returnJunctionTableInfo,
        getSimpleQuery
      )
    }
    else {
      defaultNoJunctionQuery(
        linkKey,
        fromSyntax,
        toSyntax,
        aliasedResultName,
        getSimpleQuery
      )
    }
  }

  /**
    * Query on to table only
    *
    * @param linkKey
    * @param toSyntax
    * @param aliasedResultName
    *
    * @return
    */
  private def defaultNoJunctionQuerySimple(
    linkKey: String,
    toSyntax: Option[SQLSyntaxT[TT]] = None,
    aliasedResultName: Boolean = true
  ): scalikejdbc.SQLBuilder[TT] =
  {
    defaultNoJunctionQuery(
      linkKey = linkKey,
      toSyntax = toSyntax,
      aliasedResultName = aliasedResultName,
      getSimpleQuery = true
    )
  }

  /**
    * Query on to table join on from without junction table
    *
    * @param linkKey
    * @param fromSyntax
    * @param toSyntax
    * @param aliasedResultName
    *
    * @return
    */
  private def defaultNoJunctionQuery(
    linkKey: String,
    fromSyntax: Option[SQLSyntaxT[FT]] = None,
    toSyntax: Option[SQLSyntaxT[TT]] = None,
    aliasedResultName: Boolean = true,
    getSimpleQuery: Boolean = false
  ): scalikejdbc.SQLBuilder[TT] =
  {
    //get proper from table info
    val properFromSyntax = if (fromSyntax.isDefined) fromSyntax.get
    else fromTable.defaultTable

    //get proper to table info
    val properToSyntax = if (toSyntax.isDefined) toSyntax.get
    else toTable.defaultTable

    //get proper select fields
    val selectField: scalikejdbc.SelectSQLBuilder[TT] = if (aliasedResultName)
      select(properToSyntax.resultAll)
    else select(properToSyntax.*)

    //now generate the realtion query
    val toQuery = if (getSimpleQuery) {
      selectField
        .from(toTable as properToSyntax)
        .where.eq(properToSyntax.column(toTableKey), linkKey)
    }
    else {
      selectField
        .from(toTable as properToSyntax)
        .join(fromTable as properFromSyntax)
        .on(properToSyntax.column(toTableKey), properFromSyntax.column(fromTableKey))
        .where.eq(properFromSyntax.column(fromTable.primaryKey), linkKey)
    }

    toQuery
      //join archived filter now
      .map(q => {
      toTable.archivedField match {
        case Some(s: String) => q.and.isNull(properToSyntax.column(s))
        case _ => q
      }
    })
  }

  private def defaultJunctionQuerySimple(
    linkKey: String,
    toSyntax: Option[SQLSyntaxT[TT]] = None,
    junctionSyntax: Option[SQLSyntaxT[JT]] = None,
    aliasedResultName: Boolean = true,
    returnJunctionTableInfo: Boolean = false
  ): scalikejdbc.SQLBuilder[TT] =
  {
    defaultJunctionQuery(
      linkKey = linkKey,
      toSyntax = toSyntax,
      junctionSyntax = junctionSyntax,
      aliasedResultName = aliasedResultName,
      returnJunctionTableInfo = returnJunctionTableInfo,
      getSimpleQuery = true
    )
  }

  /**
    * Default query with junction table
    *
    * @param linkKey
    * @param fromSyntax
    * @param toSyntax
    * @param junctionSyntax
    * @param aliasedResultName
    * @param returnJunctionTableInfo
    *
    * @return
    */
  private def defaultJunctionQuery(
    linkKey: String,
    fromSyntax: Option[SQLSyntaxT[FT]] = None,
    toSyntax: Option[SQLSyntaxT[TT]] = None,
    junctionSyntax: Option[SQLSyntaxT[JT]] = None,
    aliasedResultName: Boolean = true,
    returnJunctionTableInfo: Boolean = false,
    getSimpleQuery: Boolean = false
  ): scalikejdbc.SQLBuilder[TT] =
  {
    //get proper junction table info
    val properJunctionTable = junctionTable.getOrElse(throw new Exception("Junction table needs to be defined for relation"))
    val properJunctionToKey = junctionToTableKey.getOrElse(throw new Exception("Junction table needs to be defined for relation"))
    val properJunctionFromKey = junctionFromTableKey.getOrElse(throw new Exception("Junction table needs to be defined for relation"))
    val properJunctionSyntax = if (junctionSyntax.isDefined) junctionSyntax.get
    else properJunctionTable.defaultTable

    //get proper from table info
    val properFromSyntax = if (fromSyntax.isDefined) fromSyntax.get
    else fromTable.defaultTable

    //get proper to table info
    val properToSyntax = if (toSyntax.isDefined) toSyntax.get
    else toTable.defaultTable

    //get proper select fields
    val selectField: scalikejdbc.SelectSQLBuilder[TT] = (aliasedResultName, returnJunctionTableInfo) match {
      case (true, true) => select(properToSyntax.resultAll, properJunctionSyntax.resultAll)
      case (true, false) => select(properToSyntax.resultAll)
      case (false, true) => select(properToSyntax.*, properJunctionSyntax.*)
      case (false, false) => select(properToSyntax.*)
    }

    //generate the select query
    val toQuery = if (getSimpleQuery) {
      selectField
        .from(toTable as properToSyntax)
        .join(properJunctionTable as properJunctionSyntax)
        .on(properJunctionSyntax.column(properJunctionToKey), properToSyntax.column(toTableKey))
        .where.eq(properJunctionSyntax.column(properJunctionFromKey), linkKey)
    }
    else {
      selectField
        .from(toTable as properToSyntax)
        .join(properJunctionTable as properJunctionSyntax)
        .on(properJunctionSyntax.column(properJunctionToKey), properToSyntax.column(toTableKey))
        .join(fromTable as properFromSyntax)
        .on(properFromSyntax.column(fromTableKey), properJunctionSyntax.column(properJunctionFromKey))
        .where.eq(properFromSyntax.column(fromTable.primaryKey), linkKey)
    }

    toQuery
      //join archived filter now
      .map(q => {
      toTable.archivedField match {
        case Some(s: String) => q.and.isNull(properToSyntax.column(s))
        case _ => q
      }
    })
  }

  def getJoinSubQuery(
    joinType: String,
    subQueryAlias: String,
    linkKey: String,
    fromSyntax: Option[SQLSyntaxT[FT]] = None,
    toSyntax: Option[SQLSyntaxT[TT]] = None,
    junctionSyntax: Option[SQLSyntaxT[JT]] = None,
    aliasedResultName: Boolean = true,
    returnJunctionTableInfo: Boolean = false
  ): SQLSyntax =
  {
    sqls"""
    $joinType JOIN
    (
    ${
      getQuery(
        linkKey = linkKey,
        fromSyntax = fromSyntax,
        toSyntax = toSyntax,
        junctionSyntax = junctionSyntax,
        aliasedResultName = aliasedResultName,
        returnJunctionTableInfo = returnJunctionTableInfo,
        getSimpleQuery = false
      )
    }
    ) $subQueryAlias
    """
  }
}

/**
  * Enum containing relation types
  */
object RelationTypes extends Enumeration
{
  type RelationTypes = Value
  /**
    * Relation Types
    */
  val ONE_TO_ONE, ONE_TO_MANY, MANY_TO_MANY = Value
}