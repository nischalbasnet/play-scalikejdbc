package com.nischal

/**
  * Created by nbasnet on 6/24/17.
  */
object ClassHelpers
{

  /**
    * Extend Product trait used by caseclass to add toMap method to convert case class to map
    *
    * Source: https://gist.github.com/calippo/892ce793c9696b330e55772099056b7a
    *
    * @param a : Case Class instance
    * @tparam A : Type of case class
    */
  implicit class CaseClassToMap[A](val a: A) extends AnyVal
  {

    import shapeless._
    import ops.record._

    /**
      * Convert to map
      *
      * @param gen : Shapeless generic generator
      * @param tmr : Shapeless ToMap converter trait
      * @tparam L : Shapeless hlist type representation for the case class
      *
      * @return
      */
    def convertToMap[L <: HList](
      implicit gen: LabelledGeneric.Aux[A, L],
      tmr: ToMap[L]
    ): Map[String, Any] =
    {
      val m: Map[tmr.Key, tmr.Value] = tmr(gen.to(a))
      m.map { case (k: Symbol, v) => k.name -> v }
    }
  }

}
