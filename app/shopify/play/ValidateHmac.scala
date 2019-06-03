package shopify.play

import play.api.mvc._
import play.api.mvc.Results._
import shopify.hmac.{Invalid, Valid}

import scala.concurrent.{ExecutionContext, Future}

case class ValidateHmac[A](action: Action[A]) extends Action[A] with play.api.Logging {
  override def parser: BodyParser[A] = action.parser
  override def executionContext: ExecutionContext = action.executionContext

  def apply(request: Request[A]): Future[Result] = {
    HmacRequest.forQueryString(request) match {
      case Valid(validRequest) => action(validRequest)
      case Invalid => Future.successful(Forbidden("The request failed HMAC validation."))
    }
  }
}
