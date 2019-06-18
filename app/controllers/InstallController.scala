package controllers

import com.github.fulrich.scalify.ShopifyConfiguration
import com.github.fulrich.scalify.installation.{AuthorizeRedirect, InstallConfirmation}
import com.github.fulrich.scalify.play.installation.{InstallActions, InstallCallbackUri}
import javax.inject._
import play.api.cache.SyncCacheApi
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class InstallController  @Inject()(
  actions: InstallActions,
  cc: ControllerComponents,
  cache: SyncCacheApi,
  ws: WSClient,
  configuration: ShopifyConfiguration) extends AbstractController(cc) with ApplicationLogging {

  def install() = actions.install { implicit request =>
    val authorizeRedirect = AuthorizeRedirect(
      parameters = request.parameters,
      redirectUri = InstallCallbackUri(routes.InstallController.requestAccessCallback())
    )(configuration)

    cache.set(request.parameters.shop + "-nonce", authorizeRedirect.nonce)
    Redirect(authorizeRedirect.uri)
  }

  def requestAccessCallback() = actions.authorize.async { request =>
    val isNonceValid = request.parameters.validateNonce(cache.get[String](request.parameters.shop + "-nonce"))

    if (isNonceValid) requestToken(request.parameters)
    else Future.successful(InternalServerError("Could not validate the provided nonce."))
  }


  def requestToken(confirmation: InstallConfirmation): Future[Result] = {
    val jsonPayload = Json.obj(
      "client_id" -> configuration.apiKey,
        "client_secret" -> configuration.apiSecret,
      "code" -> confirmation.authorizationCode
    )

    logger.info(s"https://${confirmation.shop}/admin/oauth/access_token")
    logger.info(jsonPayload.toString)

    ws.url(s"https://${confirmation.shop}/admin/oauth/access_token").post(jsonPayload).map { result =>
      logger.info(result.body.toString)
      Ok(result.body.toString)
    }
  }
}
