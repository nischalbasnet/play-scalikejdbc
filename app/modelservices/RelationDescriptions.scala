package modelservices

import com.nischal.db.{ModelRelation, RelationTypes}
import modelservices.address.models.Address
import modelservices.users.models.{Friend, Gender, User, UserAddress}

/**
  * Created by nbasnet on 7/10/17.
  */
object RelationDescriptions
{
  /**
    * USER RELATIONS
    */
  val userGender = ModelRelation[User, Gender, Nothing](
    relationType = RelationTypes.ONE_TO_MANY,
    fromTable = User,
    fromTableKey = "gender_id",
    toTable = Gender,
    toTableKey = "gender_id"
  )

  val userAddresses = ModelRelation[User, Address, UserAddress](
    relationType = RelationTypes.MANY_TO_MANY,
    fromTable = User,
    fromTableKey = "user_id",
    toTable = Address,
    toTableKey = "address_id",
    junctionTable = Some(UserAddress),
    junctionFromTableKey = Some("user_id"),
    junctionToTableKey = Some("address_id")
  )

  val userFriends = ModelRelation[User, User, Friend](
    relationType = RelationTypes.MANY_TO_MANY,
    fromTable = User,
    fromTableKey = "user_id",
    toTable = User,
    toTableKey = "user_id",
    toTableAlias = Some("fu"),
    junctionTable = Some(Friend),
    junctionFromTableKey = Some("user_id"),
    junctionToTableKey = Some("friend_user_id")
  )
}
