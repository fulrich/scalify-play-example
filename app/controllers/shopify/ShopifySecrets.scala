package controllers.shopify

import pureconfig.{CamelCase, ConfigFieldMapping}
import pureconfig.generic.ProductHint
import pureconfig.generic.auto._

case class ShopifySecrets (
  apiKey: String,
  apiSecret: String
)

object ShopifySecrets {
  val ShopifyNamespace = "shopify"
  lazy val Default: ShopifySecrets = load()

  implicit def hint[T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

  def load(): ShopifySecrets = pureconfig.loadConfigOrThrow[ShopifySecrets](ShopifyNamespace)
}
