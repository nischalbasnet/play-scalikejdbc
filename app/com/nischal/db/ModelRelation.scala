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
  junctionToTableKey: Option[String] = None,
  toTableAlias: Option[String] = None,
  junctionTableAlias: Option[String] = None
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
    else if (toTableAlias.isDefined) toTable.syntax(toTableAlias.get)
    else toTable.defaultTable

    generateNoJunctionQuery(
      toTableKey,
      toTable,
      properToSyntax,
      fromTable,
      properFromSyntax,
      linkKey,
      aliasedResultName,
      getSimpleQuery
    )
  }

  private def generateNoJunctionQuery[TO, FROM](
    pToTableKey: String,
    pToTable: BaseModelCompanion[TO],
    pToSyntax: SQLSyntaxT[TO],
    pFromTable: BaseModelCompanion[FROM],
    pFromSyntax: SQLSyntaxT[FROM],
    pLinkKey: String,
    pAliasedResultName: Boolean,
    pGetSimpleQuery: Boolean
  ) =
  {
    //get proper select fields
    val selectField: scalikejdbc.SelectSQLBuilder[TO] = if (pAliasedResultName)
      select(pToSyntax.resultAll)
    else select(pToSyntax.*)

    //now generate the relation query
    val toQuery = if (pGetSimpleQuery) {
      selectField
        .from(pToTable as pToSyntax)
        .where.eq(pToSyntax.column(pToTableKey), pLinkKey)
    }
    else {
      selectField
        .from(pToTable as pToSyntax)
        .join(pFromTable as pFromSyntax)
        .on(pToSyntax.column(pToTableKey), pFromSyntax.column(fromTableKey))
        .where.eq(pFromSyntax.column(pFromTable.primaryKey), pLinkKey)
        .map(q => {
          fromTable.archivedField match {
            case Some(s: String) => q.and.isNull(pFromSyntax.column(s))
            case _ => q
          }
        })
    }

    toQuery
      //join archived filter now
      .map(q => {
      pToTable.archivedField match {
        case Some(s: String) => q.and.isNull(pToSyntax.column(s))
        case _ => q
      }
    })

    if (relationType == RelationTypes.ONE_TO_ONE) toQuery.limit(1)
    else toQuery
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
    else if (junctionTableAlias.isDefined) properJunctionTable.syntax(junctionTableAlias.get)
    else properJunctionTable.defaultTable

    //get proper from table info
    val properFromSyntax = if (fromSyntax.isDefined) fromSyntax.get
    else fromTable.defaultTable

    //get proper to table info
    val properToSyntax = if (toSyntax.isDefined) toSyntax.get
    else if (toTableAlias.isDefined) toTable.syntax(toTableAlias.get)
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
        .map(q => {
          properJunctionTable.archivedField match {
            case Some(s: String) => q.and.isNull(properJunctionSyntax.column(s))
            case _ => q
          }
        })
    }
    else {
      selectField
        .from(toTable as properToSyntax)
        .join(properJunctionTable as properJunctionSyntax)
        .on(properJunctionSyntax.column(properJunctionToKey), properToSyntax.column(toTableKey))
        .join(fromTable as properFromSyntax)
        .on(properFromSyntax.column(fromTableKey), properJunctionSyntax.column(properJunctionFromKey))
        .where.eq(properFromSyntax.column(fromTable.primaryKey), linkKey)
        .map(q => {
          var conQ = properJunctionTable.archivedField match {
            case Some(s: String) => q.and.isNull(properJunctionSyntax.column(s))
            case _ => q
          }

          conQ = fromTable.archivedField match {
            case Some(s: String) => conQ.and.isNull(properFromSyntax.column(s))
            case _ => conQ
          }

          conQ
        })
    }

    toQuery
      //join archived filter now
      .map(q => {
      toTable.archivedField match {
        case Some(s: String) => q.and.isNull(properToSyntax.column(s))
        case _ => q
      }
    })

    if (relationType == RelationTypes.ONE_TO_ONE || relationType == RelationTypes.MANY_TO_ONE) toQuery.limit(1)
    else toQuery
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
    import scalikejdbc.nischalmod.SqlHelpers.createSqlSyntax

    sqls""
      .append(createSqlSyntax(s"$joinType JOIN ("))
      .append(
        getQuery(
          linkKey = linkKey,
          fromSyntax = fromSyntax,
          toSyntax = toSyntax,
          junctionSyntax = junctionSyntax,
          aliasedResultName = aliasedResultName,
          returnJunctionTableInfo = returnJunctionTableInfo,
          getSimpleQuery = false
        ).toSQLSyntax
      )
      .append(createSqlSyntax(s") $subQueryAlias"))
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
  val ONE_TO_ONE, ONE_TO_MANY, MANY_TO_ONE, MANY_TO_MANY = Value
}