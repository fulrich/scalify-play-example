package controllers

import controllers.shopify.installation.{InstallRedirect, InstallRequest}
import javax.inject._
import play.api.mvc._

@Singleton
class InstallController  @Inject()(cc: ControllerComponents) extends AbstractController(cc) with ApplicationLogging {
  def install() = Action { implicit request: Request[AnyContent] =>
    val installRequest = InstallRequest(request.rawQueryString)

    installRequest match {
      case Some(parsedInstallRequest) => handleParsedInstallRequest(parsedInstallRequest)
      case None => InternalServerError("Invalid Shopify parameters.")
    }
  }

  def handleParsedInstallRequest(installRequest: InstallRequest): Result = {
    if (installRequest.valid) {
      val installRedirect = InstallRedirect(installRequest)
      Ok("Redirecting based on: " + installRequest.parameters.toString + s"Redirecting: ${installRedirect.uri}")
    }
    else {
      Forbidden("The install request failed HMAC validation.")
    }
  }
}
