package controllers

import javax.inject._
import play.api.cache.SyncCacheApi
import play.api.mvc._
import shopify.installation.{InstallParameters, AuthorizeRedirect}
import shopify.play.HmacAction
import shopify.play.installation.InstallCallbackUri


@Singleton
class InstallController  @Inject()(hmacAction: HmacAction, cc: ControllerComponents, cache: SyncCacheApi) extends AbstractController(cc) with ApplicationLogging {
  def install() = hmacAction { implicit request: Request[AnyContent] =>
    val installRedirect = AuthorizeRedirect.fromSeqMap(
      parameters = request.queryString,
      redirectUri = InstallCallbackUri(routes.InstallController.requestAccessCallback())
    )

    installRedirect match {
      case Some(parsedRedirect) => Redirect(parsedRedirect.uri)
      case None => InternalServerError("Unable to process the installation request.")
    }
  }

  def requestAccessCallback = Action { request =>
    logger.info(cache.get[AuthorizeRedirect](request.getQueryString("shop").get).get.toString)
    Ok("Calling back" + request.queryString)
  }
}
