package controllers.shopify.installation

import controllers.shopify.ShopifyHmac
import io.lemonlabs.uri.QueryString


case class InstallRequest(query: String, hmac: String, parameters: InstallParameters) {
  lazy val valid: Boolean = ShopifyHmac.validate(hmac, query)
}

object InstallRequest {
  val HmacKey = "hmac"

  def apply(query: String): Option[InstallRequest] = for {
    parsedQuery <- QueryString.parseOption(query)
    queryWithoutHmac = parsedQuery.removeAll(HmacKey).toString.replace("?", "")
    parsedHmac <- parsedQuery.param(HmacKey)
    parsedParameters <- InstallParameters(query)
  } yield InstallRequest(queryWithoutHmac, parsedHmac, parsedParameters)
}
