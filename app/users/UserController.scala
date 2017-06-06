package users

import javax.inject.Singleton

import com.nischal.base.BaseController
import play.api.mvc._

/**
  * Created by nbasnet on 6/4/17.
  */
@Singleton
class UserController extends BaseController
{
  /**
    * Get user
    * @param user_id
    * @return
    */
  def get(user_id: String) = Action {
    Ok("User returned")
  }
}
