package util

import play.api.mvc._

case class AuthenticatedRequest(val user: String, request: Request[AnyContent])
  extends WrappedRequest(request)


