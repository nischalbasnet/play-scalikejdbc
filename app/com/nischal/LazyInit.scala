package com.nischal

/**
  * trait to keep variable initialized lazily/externally
  *
  * @tparam T : Type of value
  */
trait LazyInit[T]
{
  //private variable to hold value of variable
  private var pValue: Option[T] = None

  //check if the value is initialized or not
  def isInitialized: Boolean = pValue.isDefined

  /**
    * Get the value
    *
    * @return
    */
  def get: T =
  {
    if (!isInitialized) {
      pValue = Some(setter())
    }
    pValue.get
  }

  /**
    * Just return current state even if it is uninitialized
    *
    * @return
    */
  def getOpt: Option[T] =
  {
    pValue
  }

  /**
    * method to update the variable
    *
    * @param value : value to set
    */
  private def updateValue(value: T): Unit =
  {
    if (!isInitialized) pValue = Some(value)
    else println("trying to initialize already initialized value")
  }

  /**
    * symbolic method to assign value
    *
    * @param value : value to assign
    */
  def :=(value: T): Unit =
  {
    updateValue(value)
  }

  /**
    * default value setter for
    * if the value is not set externally it will be set using setter the first time
    * user calls get
    *
    * @return
    */
  def setter: () => T
}

/**
  * Companion object
  */
object LazyInit
{
  /**
    * Create LazyInit by providing default setter fn
    *
    * @param setFn : function to set default value
    * @tparam T : type of the value
    * @return
    */
  def apply[T](setFn: () => T): LazyInit[T] = new LazyInit[T]
  {
    override def setter: () => T = setFn
  }
}