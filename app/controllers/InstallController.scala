package controllers

import javax.inject._
import play.api.cache.SyncCacheApi
import play.api.mvc._
import shopify.installation.AuthorizeRedirect
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
      case Some(parsedAuthorizeRedirect) => cacheAndRedirect(parsedAuthorizeRedirect)
      case None => InternalServerError("Unable to process the installation request.")
    }
  }

  def cacheAndRedirect(authorizeRedirect: AuthorizeRedirect): Result = {
    cache.set(nonceKey(authorizeRedirect.shop), authorizeRedirect.nonce)
    Redirect(authorizeRedirect.uri)
  }

  def nonceKey(shop: String): String = shop + "-nonce"

  def requestAccessCallback = Action { request =>
    val cachedNonce = request.getQueryString("shop").map(nonceKey).flatMap(cache.get[String].apply
    val providedNonce = request.getQueryString("nonce")

    Ok(s"Cached Nonce: $cachedNonce || Provided Nonce: $providedNonce")
  }
}
