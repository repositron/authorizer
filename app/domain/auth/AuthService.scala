package domain.auth

import play.api.{Logger, Logging}

import java.util.concurrent.ConcurrentHashMap

import javax.inject._

@Singleton
class AuthService extends Logging {
  //val logger: Logger = Logger(getClass)
  logger.info("AuthService")
  val usersMap: ConcurrentHashMap[String, User] = new ConcurrentHashMap

  def signup(userSignup: UserSignup) : Either[ValidationError, String] = {
    logger.info(s"signup ${userSignup.user_id}")
    if (usersMap.containsKey(userSignup.user_id))
      Left(AccountCreationFailed(Some("already same user_id is used")))
    else {
      val user = User(userSignup.user_id, userSignup.password, None, None)
      usersMap.put(user.userId, user)
      if (!usersMap.containsKey(userSignup.user_id))
        logger.error(s"usersMaps doesn't contain $userSignup.user_id")
      Right("Account successfully created")
    }
  }

  def getUser(userId: String) : Either[ValidationError, User] = {
    logger.info(s"getUser ${userId}")
    if (!usersMap.containsKey(userId))
      Left(UserNotFound())
    else {
      val user = usersMap.get(userId)
      Right(user)
    }
  }

  def userUpdate(userId: String, userUpdate: UserUpdate) : Either[ValidationError, User] = {
    logger.info(s"userUpdate ${userId}")
    if (!usersMap.containsKey(userId))
      Left(UserNotFound())
    else {
      val user = usersMap.get(userId)
      val updateUser = user.copy(nickName = userUpdate.nickname, comment = userUpdate.comment)
      usersMap.replace(userId, updateUser)
      Right(user)
    }
  }
}
