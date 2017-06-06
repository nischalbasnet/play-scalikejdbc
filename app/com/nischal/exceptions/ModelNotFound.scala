package com.nischal.exceptions

/**
  * Created by nbasnet on 6/4/17.
  */
class ModelNotFound(message: String) extends Exception(message)
{

}

object ModelNotFound
{
  def apply(table: String, primaryKey: String, primaryId: String): ModelNotFound =
  {
    new ModelNotFound(s"No record found for $table with $primaryKey = $primaryId")
  }
}
