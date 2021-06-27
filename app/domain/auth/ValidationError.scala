package domain.auth


sealed trait ValidationError

case class UserAlreadyExists() extends ValidationError

case class AccountCreationFailed(cause: Option[String] = None) extends ValidationError

case class UserNotFound() extends ValidationError

case class AuthenticationFailed() extends ValidationError


object ValidationError {
  def cause(validationError: ValidationError): Option[String] = {
    validationError match {
      case AccountCreationFailed(cause) => cause
      case _ => None
    }
  }

  def message(validationError: ValidationError) : String = {
    validationError match {
      case UserAlreadyExists() => "Account creation failed"
      case AccountCreationFailed(_) => "Account creation failed"
      case UserNotFound() => "No User found"
      case AuthenticationFailed() =>"Authentication Faild"
    }
  }
}