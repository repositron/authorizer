package endpoint

import domain.auth.ValidationResponse

import java.nio.charset.StandardCharsets
import java.util.Base64

object BasicAuthentication {

  // headers: Headers
  //val authorization = headers.get("Authorization")


  def authorized(authorizationStr: String)(getUserFn: (String, String) => Boolean) : Option[String] = {

    def base64Decode(str: String) : String =
      new String(Base64.getDecoder.decode(str), StandardCharsets.UTF_8)


    val decoded = authorizationStr.split(" ") match {
      case Array("Basic", encodedUserPass) => base64Decode(encodedUserPass)
      case _ => ""
    }

    decoded.split(":") match {
      case Array(user, password) => if (getUserFn(user, password)) Some(user) else None
      case _ => None
    }
  }
}
