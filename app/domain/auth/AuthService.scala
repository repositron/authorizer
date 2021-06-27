package domain.auth

class AuthService {
  def signup(userId: String, password: String) : Either[ValidationError, String] = {
    Right("Account successfully created")
  }

  def getUser(userId: String, password: String) : Either[ValidationError, User] = {
    Left(AuthenticationFailed)
  }
}
