package com.nischal

/**
  * Created by nbasnet on 6/5/17.
  */
trait Companion[T]
{
  type C

  def apply(): C
}

object Companion
{
  implicit def companion[T](implicit comp: Companion[T]) = comp()
}