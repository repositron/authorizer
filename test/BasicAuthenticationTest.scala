import endpoint.BasicAuthentication
import org.scalatest.{FunSpec, FunSuite}

class BasicAuthenticationTest extends FunSuite {
  val authFn = (u: String, p: String) => (u, p) match {
    case ("user1", "password1") => true
    case _ => false
  }

  test("valid user password") {
    assert(BasicAuthentication.authorized("Basic dXNlcjE6cGFzc3dvcmQx")(authFn))
  }

  test("invalid user password") {
    assert(!BasicAuthentication.authorized("Basic ZGFmOmRmYXM")(authFn))
  }

  test("invalid format") {
    assert(!BasicAuthentication.authorized("BasicZGFmOmRmYXM")(authFn))
  }

  test("empty") {
    assert(!BasicAuthentication.authorized("")(authFn))
  }
}
