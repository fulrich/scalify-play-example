package controllers

import com.github.fulrich.scalify.ShopifyConfiguration
import com.github.fulrich.scalify.installation.{AuthorizeConfirmation, AuthorizeRedirect, TokenRequest}
import com.github.fulrich.scalify.play.installation.json._
import com.github.fulrich.scalify.play.installation.{InstallActions, InstallCallbackUri}
import javax.inject._
import play.api.cache.SyncCacheApi
import play.api.libs.json._
import play.api.libs.ws.JsonBodyWritables._
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
    val authorizeConfirmationUri = InstallCallbackUri(routes.InstallController.requestAccessCallback())
    val authorizeRedirect = AuthorizeRedirect(request.parameters, authorizeConfirmationUri)(configuration)

    logger.info(s"Storing nonce ${authorizeRedirect.nonce}")
    logger.info(s"Redirecting to ${authorizeRedirect.uri}")

    cache.set(request.parameters.shop + "-nonce", authorizeRedirect.nonce)
    Redirect(authorizeRedirect.uri)
  }

  def requestAccessCallback() = actions.authorize.async { request =>
    val isNonceValid = request.parameters.validateNonce(cache.get[String](request.parameters.shop + "-nonce"))

    logger.info("Validating Nonce")
    if (isNonceValid) requestToken(request.parameters)
    else Future.successful(InternalServerError("Could not validate the provided nonce."))
  }

  def requestToken(confirmation: AuthorizeConfirmation): Future[Result] = {
    val tokenRequest = TokenRequest(confirmation)(configuration)

    logger.info(s"https://${confirmation.shop}/admin/oauth/access_token")
    logger.info(Json.toJson(tokenRequest).toString)

    ws.url(s"https://${confirmation.shop}/admin/oauth/access_token").post(Json.toJson(tokenRequest)).map { result =>
      logger.info(result.body.toString)
      Ok(result.body.toString)
    }
  }
}
