package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._


class InstallControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "InstallController GET" should {

    "output the parsed ShopifyRequest data" in {
      val controller = new InstallController(stubControllerComponents())
      val request = FakeRequest(GET, "/install?hmac=b46779487eaa&shop=fredsdevstore.myshopify.com&timestamp=1557768838")
      val install = controller.install().apply(request)

      status(install) mustBe OK
      contentType(install) mustBe Some("text/plain")
      contentAsString(install) must include("ShopifyRequest(hmac=b46779487eaa&shop=fredsdevstore.myshopify.com&timestamp=1557768838,b46779487eaa,fredsdevstore.myshopify.com,1557768838)")
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
