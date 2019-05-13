package controllers

import controllers.shopify.ShopifyRequest
import javax.inject._
import play.api.mvc._

@Singleton
class InstallController  @Inject()(cc: ControllerComponents) extends AbstractController(cc) with ApplicationLogging {
  def install() = Action { implicit request: Request[AnyContent] =>
    val shopify = ShopifyRequest(request.rawQueryString)

    logger.info("Parsed Request: ")
    logger.info(shopify.toString)

    shopify match {
      case Some(validShopifyRequest) => {
        validShopifyRequest.validate()
        Ok(validShopifyRequest.toString)
      }
      case None => InternalServerError("Invalid Shopify parameters.")
    }
  }
}
