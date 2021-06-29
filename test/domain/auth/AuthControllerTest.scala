package domain.auth

import endpoint.AuthController
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.Play.materializer
import play.api.libs.json.Json
import play.api.mvc.{MessagesActionBuilder, MessagesActionBuilderImpl}
import play.api.test._
import play.api.test.Helpers._

class AuthControllerTest extends PlaySpec with GuiceOneAppPerTest with Injecting {
  "AuthController POST" should {

    "should create a user" in {
      val controller = new AuthController(stubControllerComponents(), new AuthService)
      val json = Json.obj("user_id" -> "a1", "password" -> "pass1")
      //val result = controller.signup().apply(FakeRequest(POST, "/signup").withJsonBody(json))
     // val request = controller.signup().apply(FakeRequest(POST, "/signup").withJsonBody(json))
      val request = controller.signup().apply(FakeRequest(GET, "/users/ljw"))
      //val result = call(controller.signup(), request)

      //status(result) mustBe OK
      //contentType(result) mustBe Some("application/json")
      println(contentAsJson(request).prettifier)

    }
  }
}
