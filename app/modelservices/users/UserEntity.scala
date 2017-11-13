package modelservices.users

import com.nischal.LazyInit
import com.nischal.base.{BaseEntity, BaseEntityCompanion}
import modelservices.users.dao.{IUserDAO, UserDAO}
import modelservices.users.models.{Gender, User, UserAddress}
import scalikejdbc.DBSession

/**
  * Users entity class
  *
  * @param data : holds User model class
  * @param dao  : users dao
  */
case class UserEntity(
  data: User,
  dao: IUserDAO
) extends BaseEntity[User]
{
  //use the value of model like property of entity
  import data._

  //get the default db session for db calls
  implicit val defaultSession: DBSession = dao.defaultSession

  //users friends
  lazy val friends: LazyInit[Seq[User]] = LazyInit[Seq[User]](
    setFn = () => dao.getFriends(user_id)
  )
  //users gender
  lazy val gender: LazyInit[Option[Gender]] = LazyInit[Option[Gender]](
    setFn = () => gender_id.flatMap(dao.getUsersGender)
  )
  //users addresses
  lazy val addresses: LazyInit[Seq[UserAddress]] = LazyInit[Seq[UserAddress]](
    setFn = () => dao.getAddresses(user_id)
  )

  def setRelation[R](relation: Seq[R]): Unit =
  {
    relation match {
      case Gender.seqTypeCase(r) => gender := r.headOption
      case User.seqTypeCase(r) => friends := r
      case UserAddress.seqTypeCase(r) => addresses := r
      case _ => println(s"UserEntity: SETTER IS NOT DEFINED FOR => $relation")
    }
  }
}

object UserEntity extends BaseEntityCompanion[UserEntity, User, UserDAO]
{
  def apply(m: User, dao: UserDAO) = new UserEntity(m, dao)

  def apply(
    data: User,
    dao: IUserDAO,
    friends: Seq[User]
  ): UserEntity =
  {
    val user = new UserEntity(data, dao)
    user.friends := friends
    user
  }
}
