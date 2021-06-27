package domain.auth

class ValidationError(message: String)

case class UserAlreadyExists() extends ValidationError("Account creation failed")

case class AccountCreationFailed(cause: Option[String] = None)
  extends ValidationError("Account creation failed")

case class UserNotFound() extends ValidationError("No User found")

case class AuthenticationFailed() extends ValidationError("Authentication Faild")
