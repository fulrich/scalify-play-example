package controllers.installation

import com.github.fulrich.scalify.ShopifyConfiguration
import com.github.fulrich.scalify.installation.TokenRequest
import com.github.fulrich.scalify.play.installation.InstallActions
import com.github.fulrich.scalify.play.serialization.json._
import controllers.ApplicationLogging
import controllers.installation.cache.InstallCache
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables._
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class InstallConfirmationController @Inject()(
  actions: InstallActions,
  cc: ControllerComponents,
  cache: InstallCache,
  ws: WSClient,
  configuration: ShopifyConfiguration) extends AbstractController(cc) with ApplicationLogging {


  def authorize() = actions.authorize.async { request =>
    logger.info(s"Install confirmation for ${request.confirmation.shop} received.")

    request.withValidNonce(cache.getNonce(request.confirmation.shop)).async { confirmation =>
      val tokenRequest = TokenRequest(confirmation)(configuration)
      logger.info(s"Requesting Authorization Token for ${request.confirmation.shop}.")

      ws.url(TokenRequest.uri(confirmation.shop))
        .post(Json.toJson(tokenRequest))
        .map { result =>
          logger.info(s"Authorization Token for ${request.confirmation.shop} received.")
          Ok(result.body.toString)
        }
    }
  }
}
