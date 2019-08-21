package controllers

import java.time.Instant

import com.github.fulrich.scalify.ShopifyConfiguration
import com.github.fulrich.scalify.installation.{AuthorizeRedirect, InstallParameters, TokenRequest}
import com.github.fulrich.scalify.play.serialization.json._
import com.github.fulrich.scalify.play.installation.{InstallActions, InstallCallbackUri}
import javax.inject._
import play.api.cache.SyncCacheApi
import play.api.libs.json._
import play.api.libs.ws.JsonBodyWritables._
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class InstallController @Inject()(
  actions: InstallActions,
  cc: ControllerComponents,
  cache: SyncCacheApi,
  ws: WSClient,
  configuration: ShopifyConfiguration) extends AbstractController(cc) with ApplicationLogging {

  def unsafeInstall() = Action { implicit request =>
    val shop = request.getQueryString("shop")
    val installParameters = InstallParameters(shop.get, Instant.now)

    redirectToShopifyForAuthorization(installParameters)
  }

  def install() = actions.install { implicit request =>
    redirectToShopifyForAuthorization(request.parameters)
  }

  def redirectToShopifyForAuthorization(parameters: InstallParameters)(implicit request: Request[_]): Result = {
    val authorizeConfirmationUri = InstallCallbackUri(routes.InstallController.authorize())
    val authorizeRedirect = AuthorizeRedirect(parameters, authorizeConfirmationUri)(configuration)

    logger.info(s"Storing nonce ${authorizeRedirect.nonce}")
    logger.info(s"Redirecting to ${authorizeRedirect.uri}")

    cache.set(parameters.shop + "-nonce", authorizeRedirect.nonce)
    Redirect(authorizeRedirect.uri)
  }

  def authorize() = actions.authorize.async { request =>
    request.withValidNonce(cache.get[String](request.confirmation.shop + "-nonce")).async { confirmation =>
      val tokenRequest = TokenRequest(confirmation)(configuration)

      ws.url(TokenRequest.uri(confirmation.shop))
        .post(Json.toJson(tokenRequest))
        .map { result =>
          logger.info(result.body.toString)
          Ok(result.body.toString)
        }
    }
  }
}
