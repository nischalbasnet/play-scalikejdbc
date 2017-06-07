package com.nischal

import scalikejdbc.{Binders, ParameterBinderFactory}

/**
  * Created by nbasnet on 6/6/17.
  */
object SqlHelpers
{
  /**
    * Implicit binder for any val
    */
  implicit val anyParameterBinderFactory: ParameterBinderFactory[Any] = ParameterBinderFactory {
    value =>
      (stmt, idx) =>
        value match {
          case i: Int => Binders.int
          case s: String if s != "null" => Binders.string
          case s: String if s == "null" => stmt.setNull(idx, 0)
          case b: Boolean => Binders.boolean
          case b: Byte => Binders.byte
          case s: Short => Binders.short
          case l: Long => Binders.long
          case s: Float => Binders.float
          case b: BigDecimal => Binders.bigDecimal
          case d: org.joda.time.LocalDate => Binders.jodaLocalDate
          case d: org.joda.time.DateTime => Binders.jodaDateTime
          case d: org.joda.time.LocalDateTime => Binders.jodaLocalDateTime
          case d: org.joda.time.LocalTime => Binders.jodaLocalTime
          case p: java.sql.Time => Binders.sqlTime
          case p: java.sql.Timestamp => Binders.sqlTimestamp
          case p: java.sql.Array => Binders.sqlArray
          case _ => throw new NotImplementedError(s"Parameter binding is not implement in anyParameterBinderFactory for $value")
        }
  }
}
