package com.nischal

import org.joda.time.DateTime
import play.api.libs.json.{Reads, Writes, JodaReads, JodaWrites}

/**
  * Created by nbasnet on 6/10/17.
  */
object JsonReaderWriter
{
  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ"

  implicit val jodaDateReads: Reads[DateTime] = JodaReads.jodaDateReads(dateFormat)
  implicit val jodaDateWrites: Writes[DateTime] = JodaWrites.jodaDateWrites(dateFormat)
}
