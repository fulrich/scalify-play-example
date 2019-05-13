package controllers

case class ShopifyRequest(
  hmac: String,
  shop: String,
  timestamp: String
)

object ShopifyRequest {
  val HmacKey = "hmac"
  val ShopKey = "shop"
  val TimestampKey = "timestamp"

  def apply(queryMap: Map[String, Seq[String]]): Option[ShopifyRequest] = for {
    hmac <- queryMap.get(HmacKey).flatMap(_.headOption)
    shop <- queryMap.get(ShopKey).flatMap(_.headOption)
    timestamp <- queryMap.get(TimestampKey).flatMap(_.headOption)
  } yield ShopifyRequest(hmac = hmac, shop = shop, timestamp = timestamp)
}
