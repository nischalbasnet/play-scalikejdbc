package controllers

import javax.inject.{Inject, Singleton}

import modelservices.address.models.Address
import com.nischal.base.{BaseController, BaseModel}
import com.nischal.db.{ModelRelation, RelationTypes}
import play.api.libs.json.{Json, Reads, Writes}
import play.api.mvc.Action
import scalikejdbc._
import scalikejdbc.interpolation.SQLSyntax
import modelservices.users.{IUserService, models}
import modelservices.users.dao.IUserDAO
import modelservices.users.models.UserRelations.UserRelations
import modelservices.users.models._

/**
  * Created by nbasnet on 6/10/17.
  */
@Singleton
class TestController @Inject()(
  userService: IUserService,
  implicit val userDAO: IUserDAO
) extends BaseController
{
  implicit val session = AutoSession

  def test() = Action {
    val userId = "usid_1000010"

    val userDetail = userDAO.getWith(userId, Seq(UserRelations.ADDRESS, UserRelations.GENDER, UserRelations.FRIENDS))

    val userD = this.getWith(userId, Seq(UserRelationShips.ADDRESS, UserRelationShips.GENDER, UserRelationShips.FRIENDS))

    //    Ok(userDetail.toJson()(User.withFullDetail))
    Ok(userD.toJson()(User.withFullDetail))
    //        Ok(userD.statement)
  }

  def relation() = Action {
    val userId = "usid_1000010"

    val userGenderRelation = ModelRelation[User, Gender, Nothing](
      relationType = RelationTypes.ONE_TO_ONE,
      fromTable = User,
      fromTableKey = "gender_id",
      toTable = Gender,
      toTableKey = "gender_id"
    )

    val g = Gender.defaultTable
    val gender = withSQL {
      userGenderRelation.getQuery("id_male")
    }.map(userGenderRelation.defaultMapper)
      .single().apply()

    println(gender)

    val userAddress = ModelRelation[User, Address, UserAddress](
      relationType = RelationTypes.ONE_TO_MANY,
      fromTable = User,
      fromTableKey = "user_id",
      toTable = Address,
      toTableKey = "address_id",
      junctionTable = Some(UserAddress),
      junctionFromTableKey = Some("user_id"),
      junctionToTableKey = Some("address_id")
    )

    Ok(userAddress.getQuery(userId, returnJunctionTableInfo = true, aliasedResultName = false).toSQL.statement)
  }

  def getWith(user_id: String, relations: Seq[UserRelationShips.Val[_, _]])(implicit session: DBSession) =
  {
    val user: models.User.SQLSyntaxT[User] = User.defaultTable
    val fullQuery = queryGetWith(user, user_id, relations)


    val userInfo = fullQuery.map(rs => {
      val usr: User = User.fromSqlResult(user.resultName)(rs)

      //set relation value from result set
      relations.foreach(r => {
        println(s"parsed value for relation = ${r.name}")
        val tpe = r.relation.toTable

        val relationValueSet = Json.parse(rs.string(r.name)).as[Set[tpe.Model]](Reads.set(tpe.reads))
        println(relationValueSet)

        setModelRelation(usr, relationValueSet.toSeq)
      })

      usr
    }).first().apply()

    userInfo.get
  }

  import shapeless._

  val genderSeq = TypeCase[Seq[Gender]]
  val userSeq = TypeCase[Seq[User]]

  def setModelRelation[A](model: User, relation: Seq[A]): Unit =
  {
    relation match {
      case genderSeq(r) => model.setGender(r.headOption)
      case userSeq(r) => model.setFriends(r)
      case _ => println(s"SETTER IS NOT DEFINED FOR => $relation")
    }
  }

  def queryGetWith(
    user: models.User.SQLSyntaxT[User],
    user_id: String,
    relations: Seq[UserRelationShips.Val[_, _]]
  )(implicit session: DBSession): SQL[Nothing, NoExtractor] =
  {
    var selectFields = sqls"SELECT ${user.resultAll}"
    var relationJoins = sqls""
    val userFilter =
      sqls"""
            WHERE ${user.user_id} = $user_id
            AND ${user.soft_deleted} ISNULL
            GROUP BY ${user.user_id}
          """

    var relationCount = 0
    //load the relations
    relations.foreach { r =>
      import scalikejdbc.com.nischal.db.SqlHelpers.createSqlSyntax
      val subQueryAlias = s"${r.relation.fromTableKey.head}_$relationCount${r.relation.toTableKey.head}"
      //add to select field
      selectFields = selectFields.append(
        createSqlSyntax(s",json_agg(row_to_json($subQueryAlias.*)) as ${r.name} ")
      )

      //      val onQueryCondition = if (r.relation.fromTable.archivedField.isDefined) {
      //        s"$subQueryAlias.${r.relation.fromTable.archivedField.getOrElse("")} ISNULL"
      //      }
      //      else if (r.relation.toTable.columns.contains(r.relation.fromTableKey)) {
      //        s"$subQueryAlias.${r.relation.fromTableKey} = ${user.column(r.relation.fromTableKey).value}"
      //      }
      //      else ""

      relationJoins = relationJoins.append(
        sqls"""${
          r.relation.getJoinSubQuery(
            "left",
            subQueryAlias,
            user_id,
            aliasedResultName = false,
            returnJunctionTableInfo = r.returnJunctionTableInfo
          )
        }"""
          .append(
            sqls""" ON TRUE """
          )
      )
      relationCount += 1
    }

    val fullQuery =
      sql"""
           $selectFields
            FROM ${User as user}
              $relationJoins
            $userFilter
         """

    fullQuery
  }

  def address() =
  {
    val userId = "usid_1000010"

    val a = Address.defaultTable
    val ua = UserAddress.defaultTable

    val genderSubQ: SQLSyntax =
      sqls"""
        SELECT
          ${ua.resultAll}, ${a.resultAll}
        FROM ${UserAddress as ua}
          JOIN ${Address as a}
            ON ${a.address_id} = ${ua.address_id}
               AND ${a.soft_deleted} ISNULL
        WHERE ${ua.user_id} = $userId
        """

    val address = sql"$genderSubQ".map(rs => {
      val userAddress = UserAddress.fromSqlResult(ua.resultName)(rs)
      val address = Address.fromSqlResult(a.resultName)(rs)
      userAddress.setAddress(address)

      userAddress
    }).list()
      .apply()
    println(address)
  }
}
