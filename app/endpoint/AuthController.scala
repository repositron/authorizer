package endpoint

import domain.auth.{AuthService, User}
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}

import javax.inject.Inject
import scala.concurrent.Future

class AuthController @Inject()(val controllerComponents: ControllerComponents, val authService: AuthService) extends  BaseController {
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


  def signup(): Action[AnyContent] = {
    Action { implicit request =>
      signupForm.bindFromRequest.fold(
        formWithErrors => {
          BadRequest(formWithErrors.errorsAsJson)
        },
        signUp => {
          signUp
        }
      )
      authService.signup(signupForm.) match {
        case Left(error) => {
          error match {

          }
        }
      }
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
