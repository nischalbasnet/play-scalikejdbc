package scalikejdbc.nischalmod

import scalikejdbc.GeneralizedTypeConstraintsForWithExtractor.=:=
import scalikejdbc.interpolation.SQLSyntax
import scalikejdbc.{AutoSession, ConnectionPoolContext, DB, DBSession, NamedAutoSession, NamedDB, NoConnectionPoolContext, ReadOnlyAutoSession, ReadOnlyNamedAutoSession, SQLToResult, WithExtractor}

/**
  * Created by nbasnet on 6/6/17.
  */
object SqlHelpers
{

  /**
    * Provides us ability to perform insert operation using SQLToResult class
    * Needed for our string primary key
    *
    * @param sR
    * @tparam A
    * @tparam E
    * @tparam C
    */
  implicit class SQLResultExtension[A, E <: WithExtractor, C[_]](sR: SQLToResult[A, E, C])
  {
    def apply(executeWrite: Boolean)(
      implicit
      session: DBSession,
      contexts: ConnectionPoolContext = NoConnectionPoolContext,
      hasExtractors: sR.ThisSQL =:= sR.SQLWithExtractor
    ): C[A] =
    {
      val f: DBSession => C[A] = s => sR.result[A](sR.extractor, s.fetchSize(sR.fetchSize).tags(sR.tags: _*).queryTimeout(sR.queryTimeout))
      // format: OFF
      session match {
        case AutoSession => DB.autoCommit(f)
        case NamedAutoSession(name, _) => NamedDB(name, session.settings).autoCommit(f)
        case ReadOnlyAutoSession => DB.readOnly(f)
        case ReadOnlyNamedAutoSession(name, _) => NamedDB(name, session.settings).readOnly(f)
        case _ => f(session)
      }
      // format: ON
    }
  }

  def createSqlSyntax(statement: String, parameters: Seq[Any] = Nil) = SQLSyntax(statement, parameters)

  /**
    * Implicit binder for any val
    */
  //  implicit val anyParameterBinderFactory: ParameterBinderFactory[Any] = ParameterBinderFactory {
  //    value =>
  //      (stmt, idx) =>
  //        value match {
  //          case i: Int => Binders.int
  //          case s: String if s != "null" => Binders.string
  //          case s: String if s == "null" => stmt.setNull(idx, 0)
  //          case b: Boolean => Binders.boolean
  //          case b: Byte => Binders.byte
  //          case s: Short => Binders.short
  //          case l: Long => Binders.long
  //          case s: Float => Binders.float
  //          case b: BigDecimal => Binders.bigDecimal
  //          case d: org.joda.time.LocalDate => Binders.jodaLocalDate
  //          case d: org.joda.time.DateTime => Binders.jodaDateTime
  //          case d: org.joda.time.LocalDateTime => Binders.jodaLocalDateTime
  //          case d: org.joda.time.LocalTime => Binders.jodaLocalTime
  //          case p: java.sql.Time => Binders.sqlTime
  //          case p: java.sql.Timestamp => Binders.sqlTimestamp
  //          case p: java.sql.Array => Binders.sqlArray
  //          case _ => throw new NotImplementedError(s"Parameter binding is not implement in anyParameterBinderFactory for $value")
  //        }
  //  }
}
