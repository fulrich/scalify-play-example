package controllers.shopify.installation

import io.lemonlabs.uri.QueryString

case class InstallParameters(
  shop: String,
  timestamp: String
)

object InstallParameters {
  val ShopKey = "shop"
  val TimestampKey = "timestamp"

  def apply(query: String): Option[InstallParameters] = for {
    parsedQuery <- QueryString.parseOption(query)
    parsedShop <- parsedQuery.param(ShopKey)
    parsedTimestamp <- parsedQuery.param(TimestampKey)
  } yield InstallParameters(parsedShop, parsedTimestamp)
}
