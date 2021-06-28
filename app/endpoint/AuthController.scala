package endpoint

import domain.auth.{AuthService, User, UserNotFound, UserSignup, UserSignupResponse, UserSignupSuccessResponse, UserUpdate, ValidationResponse}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.Messages.implicitMessagesProviderToMessages
import play.api.i18n.MessagesApi
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsError, JsObject, JsString, JsSuccess, JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Handler, Headers, MessagesActionBuilder, MessagesRequest, Request, Result}
import play.i18n.MessagesApi
import util.AuthenticatedRequest

import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.inject.Inject

class AuthController @Inject()(messagesAction: MessagesActionBuilder, controllerComponents: ControllerComponents, authService: AuthService) extends  AbstractController(controllerComponents) {
  def index(): Action[AnyContent] = {
    Action { implicit request =>
      Ok("hello world")
    }
  }

  private val logger = Logger(getClass)

  def validationErrorResponse(error: ValidationResponse): Result = {
    val jsonMsg = Json.obj("message" -> ValidationResponse.message(error))
    ValidationResponse.cause(error) match {
      case Some(causeMessage) =>
        jsonMsg + ("cause" -> JsString(causeMessage))
      case _ =>
    }
    NotFound(jsonMsg)
  }

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
              Unauthorized
          }
        }
        case None =>
          logger.info(s"no Authenication header")
          Unauthorized
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
              /*val jsonMsg = Json.obj("message" -> ValidationResponse.message(error))
              ValidationResponse.cause(error) match {
                case Some(causeMessage) =>
                  jsonMsg + ("cause" -> JsString(causeMessage))
                case _ =>
              }
              NotFound(jsonMsg)*/
            },
            success => {
              Ok(Json.obj("message" -> success,
                "user" -> Json.obj("user_id" -> userSignup.user_id, "nickname" -> userSignup.user_id)))
            }
          )
        case JsError(errs) =>
          logger.error(s"Unable to parse payload: $errs")
          BadRequest(Json.obj("message" -> "formError1"))
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


  def updateUser(userId: String): Action[JsValue] = {
    messagesAction(parse.json) { implicit request: MessagesRequest[JsValue] =>

      logger.info(s"update received")
      request.body.validate[UserUpdate] match {
        case JsSuccess(userUpdate, _) =>
          authService.userUpdate(userId, userUpdate).fold(
            updateError => {
              val jsonMsg = Json.obj("message" -> ValidationResponse.message(updateError))
              ValidationResponse.cause(updateError) match {
                case Some(causeMessage) =>
                  jsonMsg + ("cause" -> JsString(causeMessage))
                case _ =>
              }
              NotFound(jsonMsg)
            },
            success => {
              Ok(Json.obj("message" -> "User successfully updated",
                "user" -> Json.obj("nickname" -> userUpdate.nickname, "comment" -> userUpdate.comment)))
            }
          )
        case JsError(errs) =>
          logger.error(s"Unable to parse payload: $errs")
          BadRequest(Json.obj("message" -> "formError1"))
      }
    }
  }

  def close(): Action[JsValue] = {
    messagesAction(parse.json) { implicit request: MessagesRequest[JsValue] =>
      Ok
    }
  }
}
