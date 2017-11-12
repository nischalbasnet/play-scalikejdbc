package controllers

import javax.inject.{Inject, Singleton}

import modelservices.users.UserEntity
import play.api.mvc.ControllerComponents

//import com.nischal.macros.INormModel
//import com.nischal.InsertMap
//import com.nischal.macros.NormModel
import modelservices.address.models.Address
import com.nischal.base.{BaseController, BaseModel, RelationDetail}
import com.nischal.db.{ModelRelation, RelationTypes}
//import com.nischal.macros.update

import play.api.libs.json.{Json, Reads, Writes}
import play.api.mvc.Action
import scalikejdbc.interpolation.SQLSyntax
import modelservices.users.{IUserService, models}
import modelservices.users.dao.IUserDAO
import modelservices.users.models.UserRelations.UserRelations
import modelservices.users.models._


//@NormModel
//case class TUser(
//  user_id: String,
//  @update first_name: String,
//  @update last_name: String,
//  @update email: String,
//  @update mobile_number: Option[String]
//) extends INormModel

object TUser
{

}

/**
  * Created by nbasnet on 6/10/17.
  */
@Singleton
class TestController @Inject()(
  cc: ControllerComponents,
  userService: IUserService
)(
  implicit val userDAO: IUserDAO
) extends BaseController(cc)
{

  import scalikejdbc._

  implicit val session = AutoSession

  def test() = Action {
    val userId = "usid_1000010"

    //TEST
    //    val tUser: TUser = TUser(
    //      "usr_112",
    //      "Ashim",
    //      "Adhikari",
    //      "email@email.com",
    //      None
    //    )
    //
    //    println(tUser.insertSQL())
    //END TEST

    //    val userDetail = userDAO.getWithOld(userId, Seq(UserRelations.ADDRESS, UserRelations.GENDER, UserRelations.FRIENDS))

    //    val userD = this.getWith(userId, Seq(UserRelationShips.ADDRESS, UserRelationShips.GENDER, UserRelationShips.FRIENDS))
//    val userDt = userDAO.getWith(userId, Seq(UserRelationShips.ADDRESS, UserRelationShips.GENDER, UserRelationShips.FRIENDS))

    val user = userDAO.getOrFail(userId)
    val userEntity = UserEntity(user, userDAO)
    userEntity.friends.get

    //    Ok(userDetail.toJson()(User.withFullDetail))
//    Ok(userDt.get.toJson()(User.withFullDetail))
    Ok(Json.toJson(Json.obj(
      "userInfo" -> userEntity.toJson,
      "friends" -> Json.toJson(userEntity.friends.get)
    )))
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

  def getWith(user_id: String, relations: Seq[RelationDetail[_, _, _]])(implicit session: DBSession) =
  {
    val user: models.User.SQLSyntaxT = User.defaultTable
    val fullQuery = queryGetWith(user, user_id, relations)

    val userInfo = fullQuery.map(rs => {
      val usr: User = User.fromSqlResult(rs, user.resultName)

      //set relation value from result set
      relations.foreach(r => {
        println(s"parsed value for relation = ${r.name}")
        val tpe = r.relation.toTable

        val relationValueSet = Json.parse(rs.string(r.name)).as[Set[tpe.Model]](Reads.set(tpe.reads))
        println(relationValueSet)

        User.setModelRelation(usr, relationValueSet.toSeq)
      })

      usr
    }).first().apply()

    userInfo.get
  }

  def queryGetWith(
    user: models.User.SQLSyntaxT,
    user_id: String,
    relations: Seq[RelationDetail[_, _, _]]
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
      import scalikejdbc.nischalmod.SqlHelpers.createSqlSyntax
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
      val userAddress = UserAddress.fromSqlResult(rs, ua.resultName)
      val address = Address.fromSqlResult(rs, a.resultName)
      userAddress.setAddress(address)

      userAddress
    }).list()
      .apply()
    println(address)
  }
}
