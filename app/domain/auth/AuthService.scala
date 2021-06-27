package domain.auth

import play.api.{Logger, Logging}

import java.util.concurrent.ConcurrentHashMap
import javax.inject._

@Singleton
class AuthService extends Logging {
  //val logger: Logger = Logger(getClass)
  val usersMap: ConcurrentHashMap[String, User] = new ConcurrentHashMap

  def signup(userSignup: UserSignup) : Either[ValidationError, String] = {
    logger.info(s"signup ${userSignup.user_id} ${userSignup.password}")
    if (usersMap.contains(userSignup.user_id))
      Left(AccountCreationFailed(Some("required user_id and password")))
    else {
      val user = User(userSignup.user_id, userSignup.password, None, None)
      usersMap.put(user.userId, user)
      Right("Account successfully created")
    }
  }

  def getUser(userId: String) : Either[ValidationError, User] = {
    logger.info(s"usersMap ${userId}")
    if (!usersMap.contains(userId))
      Left(UserNotFound())
    else {
      val user = usersMap.get(userId)
      Right(user)
    }
  }
}
