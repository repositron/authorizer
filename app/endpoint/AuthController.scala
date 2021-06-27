package endpoint

import domain.auth.{AuthService, User, UserSignup, UserSignupResponse, UserSignupSuccessResponse}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.Messages.implicitMessagesProviderToMessages
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsError, JsObject, JsString, JsSuccess, JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Handler, MessagesActionBuilder, MessagesRequest, Request}
import play.i18n.MessagesApi

import javax.inject.Inject
import scala.None
import scala.concurrent.Future

class AuthController @Inject()(messagesAction: MessagesActionBuilder, controllerComponents: ControllerComponents, authService: AuthService) extends  AbstractController(controllerComponents) {
  def index(): Action[AnyContent]  = {
    Action { implicit request =>
      Ok("hello world")
    }
  }

  private val logger = Logger(getClass)

  def signup() : Action[JsValue] = {
    messagesAction(parse.json) { implicit request: MessagesRequest[JsValue] =>
      logger.info(s"signup received")
      request.body.validate[UserSignup] match {
        case JsSuccess(userSignup, _) =>
          authService.signup(userSignup).fold(
            error =>  {
              BadRequest(Json.obj("message" -> "could not creat"))
            },
            success => {
              Ok(Json.obj("message" -> success,
                  "user" -> Json.obj("user_id" -> userSignup.user_id, "nickname" -> "nick")))
            }
          )
        case JsError(errs) =>
          logger.error(s"Unable to parse payload: $errs")
          BadRequest(Json.obj("message" -> "formError1"))
      }
    }
  }
  def getUser(userId: String): Action[JsValue] = {
    messagesAction(parse.json) { implicit request: MessagesRequest[JsValue] =>
      logger.info(s"get user received $userId")
      authService.getUser(userId).fold(
        error => BadRequest(Json.obj("message" -> "User details by user_id")),
        success => Ok(
          Json.obj("message" -> "User details by user_id",
            "user" -> formatGetUser(success))
        )
      )
    }
  }

  private def formatGetUser(user: User) : JsObject = {
    val nickname = user.nickName.getOrElse(user.userId)
    val jsonMsg = Json.obj("user_id" -> user.userId, "nickname" -> nickname)
    user.comment match {
      case Some(c) => jsonMsg + ("comment" -> JsString(c))
      case None => jsonMsg
    }
    //jsonMsg + ("comment" -> JsString(user.comment.get))
  }

}
