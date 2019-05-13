package controllers.shopify

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class ShopifyHmac(apiSecret: String) {
  val secretKey = new SecretKeySpec(apiSecret.getBytes, ShopifyHmac.Algorithm)

  def validate(hmac: String, payload: String): Boolean = hmac == calculateHmac(payload)

  def calculateHmac(payload: String): String = withMacInstance { macInstance =>
    hex(macInstance.doFinal(payload.getBytes))
  }

  private def hex(bytes: Seq[Byte]): String = bytes.map("%02x" format _).mkString

  private def withMacInstance[A](function: Mac => A): A = {
    val macInstance = Mac.getInstance(ShopifyHmac.Algorithm)
    macInstance.init(secretKey)

    function(macInstance)
  }
}


object ShopifyHmac {
  val Algorithm = "HmacSHA256"
  lazy val Secrets: ShopifySecrets = ShopifySecrets.load()
  lazy val Validator: ShopifyHmac = new ShopifyHmac(Secrets.apiSecret)

  def validate(hmac: String, payload: String): Boolean = Validator.validate(hmac, payload)
  def calculateHmac(payload: String): String = Validator.calculateHmac(payload)
}
