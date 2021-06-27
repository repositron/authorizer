package domain.auth

case class User(userId: String,
                password: String,
                nickName: Option[String],
                comment: Option[String])
