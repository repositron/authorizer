package endpoint

import domain.auth.{AuthService, User}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.Messages.implicitMessagesProviderToMessages
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Handler, MessagesActionBuilder, MessagesRequest, Request}
import play.i18n.MessagesApi

import javax.inject.Inject
import scala.concurrent.Future

class AuthController @Inject()(messagesAction: MessagesActionBuilder, controllerComponents: ControllerComponents, authService: AuthService) extends  AbstractController(controllerComponents) {
  def index(): Action[AnyContent]  = {
    Action { implicit request =>
      Ok("hello world")
    }
  }

  private val logger = Logger(getClass)

  private val signupForm: Form[(String, String)] = {
    import play.api.data.Forms._

    Form(
      tuple(
        "user_id" -> text,
        "password" -> text,
      )
    )
  }

  //case class SignupErrorResponse(message: String, )

  def signup() : Action[AnyContent] = {
    messagesAction { implicit request: MessagesRequest[AnyContent] =>
      logger.info(s"signup received")
      signupForm.bindFromRequest.fold(
        formWithErrors => {
          BadRequest(Json.obj("message" -> "formError1"))
        },
        signUp => {
          authService.signup(signUp._1, signUp._2).fold(
            error =>  {
              BadRequest(Json.obj("message" -> "could not creat"))
            },
            success => {
              Ok("it was ok")
            }
          )
        }
      )
    }
  }

/*  def processSignupJson[A]()(implicit request:  Request[A]): Action[AnyContent] = {
    def failure(badForm: Form[(String, String)]) = {
     BadRequest(badForm.errorsAsJson)
    }

    def success(input: Form[(String, String)]) = {
      postResourceHandler.create(input).map { post =>
        Created(Json.toJson(post)).withHeaders(LOCATION -> post.link)
      }
    }
    signupForm.bindFromRequest().fold(failure, success)
  }*/
}
