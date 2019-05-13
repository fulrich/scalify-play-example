package controllers.shopify

import pureconfig.{CamelCase, ConfigFieldMapping}
import pureconfig.generic.ProductHint
import pureconfig.generic.auto._

case class ShopifySecrets (
  apiKey: String,
  apiSecret: String
)

object ShopifySecrets {
  implicit def hint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

  lazy val Default: ShopifySecrets = pureconfig.loadConfigOrThrow[ShopifySecrets]("shopify")
}
