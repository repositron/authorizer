package domain.auth

import play.api.{Logger, Logging}
import java.util.concurrent.ConcurrentHashMap

class AuthService extends Logging {

  val users: ConcurrentHashMap[String, User] = new ConcurrentHashMap

  def signup(userSignup: UserSignup) : Either[ValidationError, String] = {
    logger.info(s"signup ${userSignup.user_id} ${userSignup.password}")
    if (users.contains(userSignup.user_id))
      Left(AccountCreationFailed(Some("required user_id and password")))
    else {
      val user = User(userSignup.user_id, userSignup.password, None, None)
      users.put(user.userId, user);
      Right("Account successfully created")
    }
  }

  def getUser(userId: String) : Either[ValidationError, User] = {
    if (!users.contains(userId))
      Left(UserNotFound())
    else {
      val user = users.get(userId)
      Right(user)
    }
  }
}
