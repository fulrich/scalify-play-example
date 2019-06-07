package shopify.play.installation

import controllers.routes
import play.api.mvc.{Call, Request, request}


object InstallCallbackUri {
  def apply(request: Request[_], action: Call): String =
    "https://" + request.host + routes.InstallController.requestAccessCallback().url

  def apply(action: Call)(implicit request: Request[_]): String =
    "https://" + request.host + routes.InstallController.requestAccessCallback().url
}
