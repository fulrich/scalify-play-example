package shopify

import pureconfig.{CamelCase, ConfigFieldMapping}
import pureconfig.generic.ProductHint
import pureconfig.generic.auto._

case class ShopifyConfiguration(
  apiKey: String,
  apiSecret: String
)

object ShopifyConfiguration {
  val ShopifyNamespace = "shopify"

  implicit def hint[T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

  def load(): ShopifyConfiguration = pureconfig.loadConfigOrThrow[ShopifyConfiguration](ShopifyNamespace)

  implicit val DefaultConfiguration: ShopifyConfiguration = load()
}
