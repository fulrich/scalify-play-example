package shopify.play

import org.scalatest.{FunSuite, Matchers}
import shopify.ShopifyConfiguration
import shopify.generators.ShopifyConfigurationGenerator
import com.github.fulrich.testcharged.generators._
import play.api.test.FakeRequest
import play.api.test.Helpers.GET
import shopify.hmac.{Invalid, ShopifyHmac, Valid}


class HmacRequestUTest extends FunSuite with Matchers {
  implicit val configuration: ShopifyConfiguration = ShopifyConfigurationGenerator().value

  test("If the request contains a valid Hmac it will be returned as a Valid[Request]") {
    val parameters = "shop=fredsdevstore.myshopify.com&timestamp=1557768838"
    val validHmac = ShopifyHmac.calculateHmac(parameters)
    val request = FakeRequest(GET, s"/install?hmac=$validHmac&$parameters")

    HmacRequest.forQueryString(request) shouldBe Valid(request)
  }

  test("If the request contains an invalid Hmac it will be returned as a Invalid") {
    val parameters = "shop=fredsdevstore.myshopify.com&timestamp=1557768838"
    val invalidHmac = Generate.alpha.value
    val request = FakeRequest(GET, s"/install?hmac=$invalidHmac&$parameters")

    HmacRequest.forQueryString(request) shouldBe Invalid
  }
}
