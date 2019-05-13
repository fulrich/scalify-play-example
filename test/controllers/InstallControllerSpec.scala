package controllers

import controllers.shopify.ShopifyHmac
import controllers.shopify.installation.{InstallParameters, InstallRedirect}
import org.scalatest.{Matchers, OptionValues}
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test.Helpers._
import play.api.test._


class InstallControllerSpec extends PlaySpec with GuiceOneServerPerSuite with Injecting with OptionValues {

  "InstallController GET" should {

    "output the parsed parameters if HMAC is valid" in {
      val controller = app.injector.instanceOf[InstallController]
      val parameters = "shop=fredsdevstore.myshopify.com&timestamp=1557768838"
      val validHmac = ShopifyHmac.calculateHmac(parameters)
      val request = FakeRequest(GET, s"/install?hmac=$validHmac&$parameters")
      val result = controller.install().apply(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value startsWith s"https://fredsdevstore.myshopify.com"
    }

    "return an internal server error if the HMAC is not valid" in {
      val controller = app.injector.instanceOf[InstallController]
      val parameters = "shop=fredsdevstore.myshopify.com&timestamp=1557768838"
      val invalidHmac = ShopifyHmac.calculateHmac("wrong_parameters")
      val request = FakeRequest(GET, s"/install?hmac=$invalidHmac&$parameters")
      val install = controller.install().apply(request)

      status(install) mustBe FORBIDDEN
      contentType(install) mustBe Some("text/plain")
      contentAsString(install) must include("The install request failed HMAC validation")
    }

    "return an internal server error if the expected shopify data could not be parsed" in {
      val controller = app.injector.instanceOf[InstallController]
      val request = FakeRequest(GET, "/install?shop=fredsdevstore.myshopify.com&timestamp=1557768838")
      val install = controller.install().apply(request)

      status(install) mustBe INTERNAL_SERVER_ERROR
      contentAsString(install) must include("Invalid Shopify parameters")
    }
  }
}
