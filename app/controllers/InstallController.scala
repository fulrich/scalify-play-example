package controllers

import controllers.shopify.installation.{InstallRedirect, InstallRequest}
import javax.inject._
import play.api.cache.SyncCacheApi
import play.api.mvc._

@Singleton
class InstallController  @Inject()(cc: ControllerComponents, cache: SyncCacheApi) extends AbstractController(cc) with ApplicationLogging {
  def install() = Action { implicit request: Request[AnyContent] =>
    val installRequest = InstallRequest(request.rawQueryString)

    installRequest match {
      case Some(parsedInstallRequest) => handleParsedInstallRequest(parsedInstallRequest)
      case None => InternalServerError("Invalid Shopify parameters.")
    }
  }

  def handleParsedInstallRequest(installRequest: InstallRequest): Result = {
    if (installRequest.valid) {
      val installRedirect = InstallRedirect(installRequest.parameters, routes.InstallController.requestAccessCallback().url)
      logger.info(s"Redirect: ${installRedirect.uri}")
      cache.set(installRequest.parameters.shop, installRedirect)
      Redirect(installRedirect.uri)
    }
    else {
      Forbidden("The install request failed HMAC validation.")
    }
  }


  def requestAccessCallback = Action { request =>
    logger.info(cache.get[InstallRedirect](request.getQueryString("shop").get).get.toString)
    Ok("Calling back" + request.queryString)
  }
}
