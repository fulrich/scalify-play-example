package controllers

import controllers.shopify.ShopifyHmac
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._


class InstallControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "InstallController GET" should {

    "output the parsed parameters if HMAC is valid" in {
      val controller = new InstallController(stubControllerComponents())
      val parameters = "shop=fredsdevstore.myshopify.com&timestamp=1557768838"
      val validHmac = ShopifyHmac.calculateHmac(parameters)
      val request = FakeRequest(GET, s"/install?hmac=$validHmac&$parameters")
      val install = controller.install().apply(request)

      status(install) mustBe OK
      contentType(install) mustBe Some("text/plain")
      contentAsString(install) must include("InstallParameters(fredsdevstore.myshopify.com,1557768838)")
    }

    "return an internal server error if the HMAC is not valid" in {
      val controller = new InstallController(stubControllerComponents())
      val parameters = "shop=fredsdevstore.myshopify.com&timestamp=1557768838"
      val invalidHmac = ShopifyHmac.calculateHmac("wrong_parameters")
      val request = FakeRequest(GET, s"/install?hmac=$invalidHmac&$parameters")
      val install = controller.install().apply(request)

      status(install) mustBe FORBIDDEN
      contentType(install) mustBe Some("text/plain")
      contentAsString(install) must include("The install request failed HMAC validation")
    }

    "return an internal server error if the expected shopify data could not be parsed" in {
      val controller = new InstallController(stubControllerComponents())
      val request = FakeRequest(GET, "/install?shop=fredsdevstore.myshopify.com&timestamp=1557768838")
      val install = controller.install().apply(request)

      status(install) mustBe INTERNAL_SERVER_ERROR
      contentAsString(install) must include("Invalid Shopify parameters")
    }
  }
}
