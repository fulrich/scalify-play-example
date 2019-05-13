package controllers.shopify.installation

import java.util.UUID

import controllers.shopify.ShopifySecrets
import io.lemonlabs.uri.Url


case class InstallRedirect(
  uri: String,
  nonce: String
)

object InstallRedirect {
  val AuthorizePath = "/admin/oauth/authorize"

  def apply(installRequest: InstallRequest): InstallRedirect = {
    val nonce = UUID.randomUUID.toString
    val urlBuilder = Url(scheme = "http", host = installRequest.parameters.shop, path = AuthorizePath)
      .addParam("client_id" -> ShopifySecrets.Default.apiKey)
      .addParam("scopes" -> "write_orders,read_customers")
      .addParam("redirect_uri" -> "here")
      .addParam("nonce" -> nonce)

    InstallRedirect(urlBuilder.toString, nonce)
  }
}
