package controllers

import javax.inject._
import play.api.cache.SyncCacheApi
import play.api.mvc._
import shopify.installation.{InstallParameters, InstallRedirect}
import shopify.play.HmacAction

@Singleton
class InstallController  @Inject()(hmacAction: HmacAction, cc: ControllerComponents, cache: SyncCacheApi) extends AbstractController(cc) with ApplicationLogging {
  def install() = hmacAction { implicit request: Request[AnyContent] =>
    val installRedirect = InstallParameters(request.rawQueryString)
      .map(InstallRedirect(routes.InstallController.requestAccessCallback().url))

    logger.info(s"Redirect: ${installRedirect}")

    installRedirect match {
      case Some(parsedRedirect) => Redirect(parsedRedirect.uri)
      case None => InternalServerError("Unable to process the installation request.")
    }
  }

  def requestAccessCallback = Action { request =>
    logger.info(cache.get[InstallRedirect](request.getQueryString("shop").get).get.toString)
    Ok("Calling back" + request.queryString)
  }
}
