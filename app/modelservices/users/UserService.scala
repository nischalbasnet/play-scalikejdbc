package modelservices.users

import javax.inject.{Inject, Singleton}

import scalikejdbc.{AutoSession, DBSession}
import modelservices.users.dao.IUserDAO
import modelservices.users.models.{Gender, User, UserAddress, UserUpdateForm}
import play.api.libs.json.{JsValue, Json, Writes}

/**
  * Created by nbasnet on 6/7/17.
  */
@Singleton
class UserService @Inject()(
  userDAO: IUserDAO
) extends IUserService
{
  implicit val session = AutoSession
  //  implicit val session = StringAutoSession

  /**
    * Get user method
    *
    * @param user_id
    * @return
    */
  def get(user_id: String): Option[User] =
  {
    userDAO.get(user_id)
  }

  /**
    * Get user by email
    *
    * @param email
    * @return
    */
  def getByEmail(email: String): Option[User] =
  {
    userDAO.getFor(email = Some(email)).headOption
  }

  /**
    * perform user password change
    *
    * @param user
    * @param newPassword
    * @return
    */
  def changeUsersPassword(user: User, newPassword: String): User =
  {
    val (encryptedPassword, generatedSalt) = encryptPassword(newPassword)
    val success = userDAO.changeUsersPassword(user, encryptedPassword, generatedSalt)

    if (success == 1) user.syncOriginal()
    else user
  }

  /**
    * function to encrypt password
    *
    * @param password
    * @return
    */
  private def encryptPassword(password: String): (String, String) =
  {
    val generatedSalt = "new_saltxxtt"

    (password, generatedSalt)
  }

  /**
    * create new user
    *
    * @param user
    * @return
    */
  def createUser(user: User): User =
  {
    val newUserId = userDAO.save(user, None)
    user.copy(user_id = newUserId)
  }

  /**
    * update user
    *
    * @param user_id
    * @param userInfo
    * @return
    */
  def updateUser(user_id: String, userInfo: UserUpdateForm): User =
  {
    val success = userDAO.saveForm(user_id, userInfo)
    //get the update user now
    userDAO.getOrFail(user_id)
  }
}

trait IUserService
{
  def get(user_id: String): Option[User]

  def getByEmail(email: String): Option[User]

  def changeUsersPassword(user: User, newPassword: String): User

  def createUser(user: User): User

  def updateUser(user_id: String, userInfo: UserUpdateForm): User
}

case class UserEntity(
  data: User,
  dao: IUserDAO
)
{
  //use the value of model like property of entity
  import data._

  implicit val defaultSession: DBSession = dao.defaultSession

  lazy val friends: LazyInit[Seq[User]] = LazyInit[Seq[User]](
    setFn = () => dao.getFriends(user_id)
  )
  lazy val gender: LazyInit[Option[Gender]] = LazyInit[Option[Gender]](
    setFn = () => dao.getUsersGender(user_id)
  )
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

  def toJson(implicit writes: Writes[User]): JsValue = data.toJson()
}

object UserEntity
{
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

trait LazyInit[T]
{
  private var pValue: Option[T] = None

  def isInitialized: Boolean = pValue.isDefined

  def get: T =
  {
    if (!isInitialized) {
      pValue = Some(setter())
    }
    pValue.get
  }

  def getOpt: Option[T] =
  {
    pValue
  }

  private def updateValue(value: T): Unit =
  {
    if (!isInitialized) pValue = Some(value)
    else println("trying to initialize already initialized value")
  }

  def :=(value: T): Unit =
  {
    updateValue(value)
  }

  def setter: () => T
}

object LazyInit
{
  def apply[T](setFn: () => T): LazyInit[T] = new LazyInit[T]
  {
    override def setter: () => T = setFn
  }
}