package domain.auth

import play.api.{Logger, Logging}

import java.util.concurrent.ConcurrentHashMap

import javax.inject._

@Singleton
class AuthService extends Logging {
  //val logger: Logger = Logger(getClass)
  logger.info("AuthService")
  val usersMap: ConcurrentHashMap[String, User] = new ConcurrentHashMap

  def signup(userSignup: UserSignup) : Either[ValidationResponse, String] = {
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

  def getUser(userId: String) : Either[ValidationResponse, User] = {
    logger.info(s"getUser ${userId}")
    if (!usersMap.containsKey(userId))
      Left(UserNotFound())
    else {
      val user = usersMap.get(userId)
      Right(user)
    }
  }

  def checkUserPassword(userId: String, password: String) : Boolean = {
    getUser(userId).map(userInfo => {
      userInfo.userId == userId && userInfo.password == password
    }).getOrElse(false)
  }

  def userUpdateNicknameComment(userId: String, nickname: Option[String], comment: Option[String]) : Either[ValidationResponse, User] = {
    logger.info(s"userUpdateNicknameComment ${userId}")
    if (!usersMap.containsKey(userId))
      Left(UserNotFound())
    else {
      val currUser = usersMap.get(userId)
      val nicknameUpdate = if (nickname.isDefined) nickname else currUser.nickName
      val commentUpdate = if (comment.isDefined) comment else currUser.comment
      var updatedUser = currUser.copy(
        comment = commentUpdate,
        nickName = nicknameUpdate
      )
      usersMap.replace(userId, updatedUser)
      Right(updatedUser)
    }
  }

  def userDelete(userId: String): ValidationResponse = {
    logger.info(s"userDelete ${userId}")

    if (usersMap.remove(userId) == null)
        UserNotFound()
    else
      AccountRemovedSuccessfully()
  }
}
