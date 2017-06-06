package users.dao

import javax.inject.{Inject, Singleton}

import com.nischal.base.BasePostgresDAO
import com.nischal.exceptions.ModelNotFound
import users.models.{User, UsersCompanion}

/**
  * Created by nbasnet on 6/4/17.
  */
@Singleton
class UserPostgresDAO @Inject()(
  usersCompanion: UsersCompanion
) extends BasePostgresDAO[User, UsersCompanion] with IUserPostgresDAO
{
  /**
    * Needed as pattern match on generic T does not work
    *
    * @param optionModel
    * @param primaryId
    *
    * @return
    */
  override def modelFailMatch(optionModel: Option[User], primaryId: String): User = optionModel match {
    case Some(model: User) => model
    case None => throw ModelNotFound(modelCompanion.tableName, modelCompanion.primaryKey, primaryId)
  }

  /**
    * Companion class for model object
    *
    * @return
    */
  override val modelCompanion: UsersCompanion = usersCompanion
}
