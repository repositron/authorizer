package util

import play.api.libs.json._
import play.api.mvc._
import play.api.mvc.Results.BadRequest
import play.api.mvc.Action

case class AuthenticatedRequest(val user: String, request: Request[AnyContent])
  extends WrappedRequest(request)


