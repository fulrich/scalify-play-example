package controllers.installation

import java.time.Instant

import com.github.fulrich.scalify.ShopifyConfiguration
import com.github.fulrich.scalify.installation.{AuthorizeRedirect, InstallParameters}
import com.github.fulrich.scalify.play.installation.{InstallActions, InstallCallbackUri}
import controllers.ApplicationLogging
import controllers.installation.cache.InstallCache
import javax.inject.{Inject, Singleton}
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.{Environment, Mode}


@Singleton
class InstallRequestController  @Inject()(
  actions: InstallActions,
  cc: ControllerComponents,
  cache: InstallCache,
  ws: WSClient,
  configuration: ShopifyConfiguration,
  environment: Environment) extends AbstractController(cc) with ApplicationLogging {

  def unsafeInstall(shop: String) = Action { implicit request =>
    environment.mode match {
      case Mode.Dev | Mode.Test => redirectToShopifyForAuthorization(InstallParameters(shop, Instant.now))
      case Mode.Prod => Forbidden("Unsafe Install only available in developer mode.")
    }
  }

  def install() = actions.install { implicit request =>
    redirectToShopifyForAuthorization(request.parameters)
  }

  private def redirectToShopifyForAuthorization(parameters: InstallParameters)(implicit request: Request[_]): Result = {
    val authorizeConfirmationUri = InstallCallbackUri(routes.InstallConfirmationController.authorize())
    val authorizeRedirect = AuthorizeRedirect(parameters, authorizeConfirmationUri)(configuration)

    logger.info(s"Install request for ${parameters.shop} received.")

    cache.setNonce(parameters.shop, authorizeRedirect.nonce)
    Redirect(authorizeRedirect.uri)
  }
}
