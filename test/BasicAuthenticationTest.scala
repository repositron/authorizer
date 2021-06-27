import endpoint.BasicAuthentication
import org.scalatest.{FunSpec, FunSuite}

class BasicAuthenticationTest extends FunSuite {
  val authFn = (u: String, p: String) => (u, p) match {
    case ("user1", "password1") => true
    case _ => false
  }

  test("dXNlcjE6cGFzc3dvcmQx") {
    assert(BasicAuthentication.authorized("Basic dXNlcjE6cGFzc3dvcmQx")(authFn))
  }
}
