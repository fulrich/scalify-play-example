package controllers

import javax.inject._
import play.api.cache.SyncCacheApi
import play.api.mvc._
import shopify.installation.AuthorizeRedirect
import shopify.play.HmacAction
import shopify.play.installation.InstallCallbackUri


@Singleton
class InstallController  @Inject()(hmacAction: HmacAction, cc: ControllerComponents, cache: SyncCacheApi) extends AbstractController(cc) with ApplicationLogging {
  def install = hmacAction { implicit request: Request[AnyContent] =>
    val installRedirect = AuthorizeRedirect.fromSeqMap(
      parameters = request.queryString,
      redirectUri = InstallCallbackUri(routes.InstallController.requestAccessCallback())
    )

    installRedirect match {
      case Some(parsedAuthorizeRedirect) => cacheAndRedirect(parsedAuthorizeRedirect)
      case None => InternalServerError("Unable to process the installation request.")
    }
  }

  def nonceKey(shop: String): String = shop + "-nonce"

  def cacheAndRedirect(authorizeRedirect: AuthorizeRedirect): Result = {
    cache.set(nonceKey(authorizeRedirect.shop), authorizeRedirect.nonce)
    Redirect(authorizeRedirect.uri)
  }

  def requestAccessCallback = hmacAction { request =>
    val cachedNonceOption = request.getQueryString("shop").map(nonceKey).flatMap(cache.get[String])
    val providedNonceOption = request.getQueryString("state")

    val nonceConfirmation = for {
      cachedNonce <- cachedNonceOption
      providedNonce <- providedNonceOption
    } yield cachedNonce == providedNonce

    nonceConfirmation match {
      case Some(true) => Ok("Installation Successful!")
      case Some(false) => InternalServerError("Could not validate the provided nonce.")
      case None => InternalServerError("Some required information was not available.")
    }
  }
}
