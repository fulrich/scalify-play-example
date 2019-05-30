package shopify.play

import javax.inject.Inject
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


class HmacAction @Inject()(parser: BodyParsers.Default)(implicit ec: ExecutionContext) extends ActionBuilderImpl(parser) {
  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = block(request)
  override def composeAction[A](action: Action[A]) = ValidateHmac(action)
}
