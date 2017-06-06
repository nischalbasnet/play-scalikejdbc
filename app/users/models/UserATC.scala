package users.models

import play.api.libs.json.Json

/**
  * Created by nbasnet on 6/5/17.
  */
trait UserATC
{
  self: User =>

  def toJson() = Json.toJson(this)
}
