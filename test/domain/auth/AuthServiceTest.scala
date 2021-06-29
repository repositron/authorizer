package domain.auth

import org.scalatest.FunSuite

class AuthServiceTest extends FunSuite {

  def fixture =
    new {
      val authService = new AuthService
      val user: Either[ValidationResponse, String] = authService.signup(UserSignup("user1", "pass1"))
    }

  test("testUserUpdateComment") {
    val f = fixture
    val userUpdate = f.authService.userUpdateNicknameComment("user1", None,  Some("comment1"))
    assertResult(User("user1", "pass1", None, Some("comment1")))(userUpdate.getOrElse(fail("either was not Right!")))
  }

  test("testUserUpdateNicknameAmdComment") {
    val f = fixture
    val userUpdate = f.authService.userUpdateNicknameComment("user1", Some("nick1"),  Some("comment1"))
    assertResult(User("user1", "pass1", Some("nick1"), Some("comment1")))(userUpdate.getOrElse(fail("either was not Right!")))
  }

  test("checkUserPassword user invalid password") {
    val f = fixture
    assert(!f.authService.checkUserPassword("user1", "abced"))
  }

  test("checkUserPassword user valid password") {
    val f = fixture
    assert(f.authService.checkUserPassword("user1", "pass1"))
  }
}
