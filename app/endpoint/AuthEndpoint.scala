package endpoint

import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing._
import play.api.routing.sird._

import javax.inject.Inject

class AuthEndpoint @Inject()(authController: AuthController) extends SimpleRouter {
  override def routes: Routes = {
    case POST(p"/signup") =>  authController.signup()
    case GET(p"/signup/$to") =>  authController.getUser(to)
    case GET(p"/") => authController.index()
  }
}
