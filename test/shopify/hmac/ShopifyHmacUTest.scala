package shopify.hmac

import com.github.fulrich.testcharged.generators._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}
import shopify.generators.ShopifyConfigurationGenerator


class ShopifyHmacUTest extends FunSuite with Matchers with GeneratorDrivenPropertyChecks{
  test("ShopifyHmac can validate an HMAC value using the same key") {
    forAll(ShopifyConfigurationGenerator()) { implicit config =>
      val shopifyHmac = new ShopifyHmac()
      val payload = Generate.alphaNumeric.value
      val expectedHmac = shopifyHmac.calculateHmac(payload)

      shopifyHmac.validate(expectedHmac, payload) shouldBe true
    }
  }

  test("ShopifyHmac has a convenience methods on the object can validate and calculate HMAC") {
    forAll(ShopifyConfigurationGenerator()) { payload =>
      val payload = Generate.alphaNumeric.value
      val expectedHmac = ShopifyHmac.calculateHmac(payload)

      ShopifyHmac.validate(expectedHmac, payload) shouldBe true
    }
  }
}
