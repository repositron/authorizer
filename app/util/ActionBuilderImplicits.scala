package util

import play.api.libs.json._
import play.api.mvc.{ActionBuilder, ControllerComponents, Request}
import play.api.mvc.Results.BadRequest

import scala.concurrent.ExecutionContext

trait ActionBuilderImplicits {
  implicit class ExActionBuilder[P](actionBuilder: ActionBuilder[Request, P])(implicit cc: ControllerComponents) {

    def validateJson[A](implicit executionContext: ExecutionContext, reads: Reads[A]): ActionBuilder[Request, A] = {
      actionBuilder(cc.parsers.tolerantJson.validate(jsValue => {
        jsValue.validate.asEither.left
          .map(errors => BadRequest(JsError.toJson(errors)))
      }))
    }
  }
}

object ActionBuilderImplicits extends ActionBuilderImplicits
