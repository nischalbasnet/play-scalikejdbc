package users.dao

import javax.inject.{Inject, Singleton}

import com.nischal.base.BaseDAO
import com.nischal.exceptions.ModelNotFound
import users.models.{User, UsersCompanion}

/**
  * Created by nbasnet on 6/4/17.
  */
@Singleton
class UserPostgresDAO @Inject()(
  usersCompanion: UsersCompanion
) extends BaseDAO[User, UsersCompanion] with IUserReadDAO with IUserWriteDAO
{

  override def modelFailMatch(optionModel: Option[User], primaryId: String): User = optionModel match {
    case Some(model: User) => model
    case None => throw ModelNotFound(modelCompanion.tableName, modelCompanion.primaryKey, primaryId)
  }

  override def modelCompanion: UsersCompanion = usersCompanion
}
