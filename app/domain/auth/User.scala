package domain.auth

import play.api.libs.json.{Format, Json}

case class User(userId: String,
                password: String,
                nickName: Option[String],
                comment: Option[String])


case class UserSignup(user_id: String, password: String)
object UserSignup {
  implicit val formats: Format[UserSignup] = Json.format[UserSignup]
}

case class UserSignupResponse(user_id: String, nickname: String)
case class UserSignupSuccessResponse(message: String, user: UserSignupResponse)

object UserSignupSuccessResponse {
  implicit val format1: Format[UserSignupResponse] = Json.format[UserSignupResponse]
  implicit val format2: Format[UserSignupSuccessResponse] = Json.format[UserSignupSuccessResponse]
}


case class UserUpdate(nickname: Option[String], comment: Option[String],
                      user_id: Option[String], password : Option[String])
object UserUpdate {
  implicit val formats: Format[UserUpdate] = Json.format[UserUpdate]
}