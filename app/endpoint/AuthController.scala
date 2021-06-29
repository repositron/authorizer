package endpoint

import domain.auth.{AccountCreationFailed, AccountRemovedSuccessfully, AuthService, AuthenticationFailed, User, UserNotFound, UserSignup, UserSignupResponse, UserSignupSuccessResponse, UserUpdate, UserUpdateFailed, ValidationResponse}
import play.api.Logger
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsError, JsObject, JsString, JsSuccess, JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, MessagesActionBuilder, MessagesRequest, Request, Result}
import util.AuthenticatedRequest
import javax.inject.Inject

class AuthController @Inject()(messagesAction: MessagesActionBuilder, controllerComponents: ControllerComponents, authService: AuthService) extends  AbstractController(controllerComponents) {
  private val logger = Logger(getClass)

  def Authenticated(f: AuthenticatedRequest => Result): Action[AnyContent] = {
    Action { request =>
      request.headers.get("Authorization") match {
        case Some(auth) => {
          BasicAuthentication.authorized(auth)(authService.check) match {
            case Some(user) =>
              logger.info(s"Authenticated $user")
              f(AuthenticatedRequest(user, request))
            case None =>
              logger.info(s"unauthorised")
              Unauthorized(Json.obj(
                "message" -> ValidationResponse.message(AuthenticationFailed())))
          }
        }
        case None =>
          logger.info(s"no Authorization header")
          Unauthorized(Json.obj(
            "message" -> ValidationResponse.message(AuthenticationFailed())))
      }
    }
  }

  def signup(): Action[JsValue] = {
    messagesAction(parse.json) { implicit request: MessagesRequest[JsValue] =>
      logger.info(s"signup received")
      request.body.validate[UserSignup] match {
        case JsSuccess(userSignup, _) =>
          authService.signup(userSignup).fold(
            error => {
              validationErrorResponse(error)
            },
            success => {
              Ok(Json.obj("message" -> success,
                "user" -> Json.obj("user_id" -> userSignup.user_id, "nickname" -> userSignup.user_id)))
            }
          )
        case JsError(errs) =>
          logger.error(s"Unable to parse payload: $errs")
          badRequestResponse(AccountCreationFailed(Some("required user_id and password")))
      }
    }
  }

  def getUser(userId: String): Action[AnyContent] = {
    Authenticated { implicit request =>
      logger.info(s"get user received $userId")
      authService.getUser(userId).fold(
        error => {
          NotFound(Json.obj("message" -> ValidationResponse.message(error)))
        },
        success => Ok(
          Json.obj("message" -> "User details by user_id",
            "user" -> formatGetUser(success))
        )
      )
    }
  }

  private def formatGetUser(user: User): JsObject = {
    val nickname = user.nickName.getOrElse(user.userId)
    val jsonMsg = Json.obj("user_id" -> user.userId, "nickname" -> nickname)
    user.comment match {
      case Some(c) => jsonMsg + ("comment" -> JsString(c))
      case None => jsonMsg
    }
  }

  def updateUser(userId: String): Action[AnyContent] = {
    def processJsonRequest(userUpdate: UserUpdate) : Result = {
      if (userUpdate.user_id.isDefined || userUpdate.password.isDefined) {
        badRequestResponse(UserUpdateFailed(Some("not updatable user_id and password")))
      }
      else if (userUpdate.nickname.isEmpty && userUpdate.comment.isEmpty) {
        badRequestResponse(UserUpdateFailed(Some("required nickname or comment")))
      }
      else {
        authService.userUpdateNicknameComment(userId, userUpdate.nickname, userUpdate.comment).fold(
          updateError => {
            validationErrorResponse(updateError)
          },
          success => {
            Ok(Json.obj("message" -> "User successfully updated",
              "recipe" -> Json.arr("nickname" -> userUpdate.nickname, "comment" -> userUpdate.comment))
            )
          }
        )
      }
    }

    Authenticated { implicit request =>
      logger.info(s"update received")
      request.body.asJson match {
        case Some(bodyAsJson) => {
          bodyAsJson.validate[UserUpdate] match {
            case JsSuccess(userUpdate, _) =>
              processJsonRequest(userUpdate)
            case JsError(errs) =>
              logger.error(s"Unable to parse payload: $errs")
              validationErrorResponse(UserNotFound())
          }
        }
        case None => validationErrorResponse(UserNotFound())
      }
    }
  }

  def close(): Action[AnyContent] = {
    Authenticated { implicit request =>
      logger.info(s"closed received")
      // use user from Authenticated
      authService.userDelete(request.user) match {
        case validation @ AccountRemovedSuccessfully() =>
          Ok(Json.obj("message" -> ValidationResponse.message(validation)))
          // TODO close
        case _ =>
          // should not happen because user wouldn't be authorised
          NotFound(Json.obj("message" -> ValidationResponse.message(UserNotFound())))
      }
    }
  }

  private def validationErrorResponse(error: ValidationResponse): Result = {
    val jsonMsg = Json.obj("message" -> ValidationResponse.message(error))
    ValidationResponse.cause(error) match {
      case Some(causeMessage) =>
        NotFound(jsonMsg + ("cause" -> JsString(causeMessage)))
      case _ =>
        NotFound(jsonMsg)
    }

  }

  private def badRequestResponse(error: ValidationResponse) : Result = {
    val jsonMsg = Json.obj("message" -> ValidationResponse.message(error))
    ValidationResponse.cause(error) match {
      case Some(causeMessage) =>
        BadRequest(jsonMsg + ("cause" -> JsString(causeMessage)))
      case _ =>
        BadRequest(jsonMsg)
    }
  }
}
