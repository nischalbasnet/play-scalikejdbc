package com.nischal.implicits

import scala.collection.AbstractSeq
import scala.collection.generic.SeqFactory

object IteratorImplicits
{

  implicit class IteratorHelpers[A](val a: Iterable[A]) extends AnyVal
  {
    /**
      * Allow for foreach to have incremental index
      *
      * @param f : function to run in each iteration
      * @tparam U
      */
    @inline final def forEachWithIndex[U](f: (Int, A) => U): Unit =
    {
      var index = -1
      a.foreach(a => {
        index = index + 1
        f(index, a)
      })
    }
  }

}
