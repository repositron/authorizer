package domain.auth

sealed trait ValidationError extends Product with Serializable
case object UserAlreadyExists extends ValidationError
case object AccountCreationFailed extends ValidationError
case object UserNotFound extends ValidationError
case object AuthenticationFailed extends ValidationError
