package controllers

import javax.inject._
import play.api.Logger
import play.api.cache.SyncCacheApi
import play.api.libs.ws.WSClient
import play.api.mvc._
import shopify.installation.AuthorizeRedirect
import shopify.play.HmacAction
import shopify.play.installation.InstallCallbackUri
import play.api.libs.json._
import shopify.ShopifyConfiguration

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class InstallController  @Inject()(hmacAction: HmacAction, cc: ControllerComponents, cache: SyncCacheApi, ws: WSClient) extends AbstractController(cc) with ApplicationLogging {

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

  def requestAccessCallback = hmacAction.async { request =>
    val cachedNonceOption = request.getQueryString("shop").map(nonceKey).flatMap(cache.get[String])
    val providedNonceOption = request.getQueryString("state")

    val nonceConfirmation = for {
      cachedNonce <- cachedNonceOption
      providedNonce <- providedNonceOption
    } yield cachedNonce == providedNonce

    nonceConfirmation match {
      case Some(true) => requestToken(request)
      case Some(false) => Future.successful(InternalServerError("Could not validate the provided nonce."))
      case None => Future.successful(InternalServerError("Some required information was not available."))
    }
  }

  def requestToken(request: Request[_]): Future[Result] = {
    val shop = request.getQueryString("shop").get
    val code = request.getQueryString("code").get

    val jsonPayload = Json.obj(
      "client_id" -> ShopifyConfiguration.DefaultConfiguration.apiKey,
        "client_secret" -> ShopifyConfiguration.DefaultConfiguration.apiSecret,
      "code" -> code
    )

    logger.info(s"https://$shop.myshopify.com/admin/oauth/access_token")
    logger.info(jsonPayload.toString)

    ws.url(s"https://$shop.myshopify.com/admin/oauth/access_token").post(jsonPayload).map { result =>
      logger.info(result.body.toString)
      Ok(result.body.toString)
    }
  }
}
