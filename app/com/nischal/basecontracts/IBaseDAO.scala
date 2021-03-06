package com.nischal.basecontracts

import scalikejdbc.DBSession

/**
  * Created by nbasnet on 6/4/17.
  */
trait IBaseDAO[MT, PT] extends IBaseReadDAO[MT, PT] with IBaseWriteDAO[MT, PT]
{

}

trait IBaseReadDAO[MT, PT]
{
  def get(primaryId: PT)(implicit session: DBSession): Option[MT]

  def getMany(primaryIds: Seq[PT])(implicit session: DBSession): Seq[MT]

  def getOrFail(primaryId: PT)(implicit session: DBSession): MT
}

trait IBaseWriteDAO[MT, PT]
{
  def save(model: MT, primaryId: Option[PT])(implicit session: DBSession): PT

  def saveMany(model: Seq[MT], primaryId: Seq[PT])(implicit session: DBSession): Seq[PT]
}