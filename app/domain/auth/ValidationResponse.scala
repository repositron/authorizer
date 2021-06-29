package domain.auth


sealed trait ValidationResponse

case class UserAlreadyExists() extends ValidationResponse

case class AccountCreationFailed(cause: Option[String] = None) extends ValidationResponse

case class UserNotFound() extends ValidationResponse

case class AuthenticationFailed() extends ValidationResponse

case class AccountRemovedSuccessfully() extends ValidationResponse

case class UserUpdateFailed(cause: Option[String] = None) extends ValidationResponse

case class NoPermissionForUpdate() extends ValidationResponse


object ValidationResponse {
  def cause(validationError: ValidationResponse): Option[String] = {
    validationError match {
      case AccountCreationFailed(cause) => cause
      case UserUpdateFailed(cause) => cause
      case _ => None
    }
  }

  def message(validationError: ValidationResponse) : String = {
    validationError match {
      case UserAlreadyExists() => "Account creation failed"
      case AccountCreationFailed(_) => "Account creation failed"
      case UserNotFound() => "No User found"
      case AuthenticationFailed() =>"Authentication Faild"
      case AccountRemovedSuccessfully() => "Account and user successfully removed"
      case UserUpdateFailed(_) => "User updation failed"
      case NoPermissionForUpdate() => "No Permission for Update"
    }
  }
}
