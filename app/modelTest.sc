import org.joda.time.DateTime
import scalikejdbc.ParameterBinder
import scalikejdbc.interpolation.SQLSyntax
import shapeless.{HNil, LabelledGeneric}
import shapeless.syntax.singleton._
import shapeless.record._
import modelservices.users.models.User

val user = User(
  user_id = "uid_112",
  first_name = "Nischal",
  last_name = "Basnet",
  email = "nischal@outlook.com",
  mobile_number = "444-333-2343",
  image = "man.png",
  password = "pw",
  salt = "salt",
  gender_id = "id_male",
  created = new DateTime(),
  updated = new DateTime(),
  soft_deleted = None
)

val userGen = LabelledGeneric[User]

val rec = userGen.to(user)
val s: Symbol = Symbol("user_id")

def removeNone[T](opt: Option[T]) = opt match {
  case None => None
  case Some(n: T@unchecked) => n
}

val book =
  ("author" ->> Some("Benjamin Pierce")) ::
    ("title"  ->> Some("Types and Programming Languages")) ::
    ("id"     ->>  Some(262162091)) ::
    ("price"  ->>  None) ::
    HNil

book + ("author" ->> Some("Nischal"))


