package shopify.play

import play.api.mvc.Request
import shopify.ShopifyConfiguration
import shopify.hmac.{Hmac, HmacQueryString}


object HmacRequest {
  def forQueryString[A](request: Request[A])(implicit shopifyConfiguration: ShopifyConfiguration): Hmac[Request[A]] = {
    val hmacQueryString = HmacQueryString(request.rawQueryString)
    val hmac = Hmac(hmacQueryString.hmac, hmacQueryString.queryString)

    hmac.map(_ => request)
  }
}
