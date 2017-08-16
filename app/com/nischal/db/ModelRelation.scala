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
    toTable.fromSqlResult(rs, toTable.defaultTable.resultName)
  }

  /**
    * Get the query
    *
    * @param linkId
    * @param fromSyntax
    * @param toSyntax
    * @param junctionSyntax
    * @param aliasedResultName
    * @param returnJunctionTableInfo
    *
    * @return
    */
  def getQuery(
    linkId: String,
    fromSyntax: Option[SQLSyntaxT[FT]] = None,
    toSyntax: Option[SQLSyntaxT[TT]] = None,
    junctionSyntax: Option[SQLSyntaxT[JT]] = None,
    aliasedResultName: Boolean = true,
    returnJunctionTableInfo: Boolean = false,
    dontJoinFromTable: Boolean = true
  ): scalikejdbc.SQLBuilder[TT] =
  {
    if (_overriddenQuery.isDefined) {
      _overriddenQuery.get
    }
    else if (junctionTable.isDefined) {
      defaultJunctionQuery(
        linkId,
        fromSyntax,
        toSyntax,
        junctionSyntax,
        aliasedResultName,
        returnJunctionTableInfo,
        dontJoinFromTable
      )
    }
    else {
      defaultNoJunctionQuery(
        linkId,
        fromSyntax,
        toSyntax,
        aliasedResultName,
        dontJoinFromTable
      )
    }
  }

  /**
    * Query on to table only
    *
    * @param linkId
    * @param toSyntax
    * @param aliasedResultName
    *
    * @return
    */
  private def defaultNoJunctionQuerySimple(
    linkId: String,
    toSyntax: Option[SQLSyntaxT[TT]] = None,
    aliasedResultName: Boolean = true
  ): scalikejdbc.SQLBuilder[TT] =
  {
    defaultNoJunctionQuery(
      linkId = linkId,
      toSyntax = toSyntax,
      aliasedResultName = aliasedResultName,
      dontJoinFromTable = true
    )
  }

  /**
    * Query on to table join on from without junction table
    *
    * @param linkId
    * @param fromSyntax
    * @param toSyntax
    * @param aliasedResultName
    *
    * @return
    */
  private def defaultNoJunctionQuery(
    linkId: String,
    fromSyntax: Option[SQLSyntaxT[FT]] = None,
    toSyntax: Option[SQLSyntaxT[TT]] = None,
    aliasedResultName: Boolean = true,
    dontJoinFromTable: Boolean = false
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
      linkId,
      aliasedResultName,
      dontJoinFromTable
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
    pDontJoinFromTable: Boolean
  ) =
  {
    //get proper select fields
    val selectField: scalikejdbc.SelectSQLBuilder[TO] = if (pAliasedResultName)
      select(pToSyntax.resultAll)
    else select(pToSyntax.*)

    //now generate the relation query
    val toQuery = if (pDontJoinFromTable) {
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
    linkId: String,
    toSyntax: Option[SQLSyntaxT[TT]] = None,
    junctionSyntax: Option[SQLSyntaxT[JT]] = None,
    aliasedResultName: Boolean = true,
    returnJunctionTableInfo: Boolean = false
  ): scalikejdbc.SQLBuilder[TT] =
  {
    defaultJunctionQuery(
      linkId = linkId,
      toSyntax = toSyntax,
      junctionSyntax = junctionSyntax,
      aliasedResultName = aliasedResultName,
      returnJunctionTableInfo = returnJunctionTableInfo,
      joinFromTable = true
    )
  }

  /**
    * Default query with junction table
    *
    * @param linkId
    * @param fromSyntax
    * @param toSyntax
    * @param junctionSyntax
    * @param aliasedResultName
    * @param returnJunctionTableInfo
    *
    * @return
    */
  private def defaultJunctionQuery(
    linkId: String,
    fromSyntax: Option[SQLSyntaxT[FT]] = None,
    toSyntax: Option[SQLSyntaxT[TT]] = None,
    junctionSyntax: Option[SQLSyntaxT[JT]] = None,
    aliasedResultName: Boolean = true,
    returnJunctionTableInfo: Boolean = false,
    joinFromTable: Boolean = false
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
    val toQuery = if (joinFromTable) {
      selectField
        .from(toTable as properToSyntax)
        .join(properJunctionTable as properJunctionSyntax)
        .on(properJunctionSyntax.column(properJunctionToKey), properToSyntax.column(toTableKey))
        .where.eq(properJunctionSyntax.column(properJunctionFromKey), linkId)
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
        .where.eq(properFromSyntax.column(fromTable.primaryKey), linkId)
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
    linkId: String,
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
          linkId = linkId,
          fromSyntax = fromSyntax,
          toSyntax = toSyntax,
          junctionSyntax = junctionSyntax,
          aliasedResultName = aliasedResultName,
          returnJunctionTableInfo = returnJunctionTableInfo,
          dontJoinFromTable = false
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