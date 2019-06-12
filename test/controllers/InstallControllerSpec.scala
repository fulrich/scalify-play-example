package controllers

import com.github.fulrich.scalify.hmac.ShopifyHmac
import injection.InjectedConfiguration
import org.scalatest.OptionValues
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test.Helpers._
import play.api.test._


class InstallControllerSpec extends PlaySpec with GuiceOneServerPerSuite with InjectedConfiguration with OptionValues {
  "InstallController GET" should {
    "output the parsed parameters if HMAC is valid" in {
      val controller = app.injector.instanceOf[InstallController]
      val parameters = "shop=fredsdevstore.myshopify.com&timestamp=1557768838"
      val validHmac = ShopifyHmac.calculateHmac(parameters)
      val request = FakeRequest(GET, s"/install?hmac=$validHmac&$parameters")
      val result = controller.install().apply(request)

      status(result) mustBe SEE_OTHER
      val redirectUri = redirectLocation(result).value
      redirectUri must startWith (s"https://fredsdevstore.myshopify.com")
      redirectUri must include (Configuration.scopes.mkString(","))
    }

    "return an internal server error if the HMAC is not valid" in {
      val controller = app.injector.instanceOf[InstallController]
      val parameters = "shop=fredsdevstore.myshopify.com&timestamp=1557768838"
      val invalidHmac = ShopifyHmac.calculateHmac("wrong_parameters")
      val request = FakeRequest(GET, s"/install?hmac=$invalidHmac&$parameters")
      val install = controller.install().apply(request)

      status(install) mustBe FORBIDDEN
      contentType(install) mustBe Some("text/plain")
      contentAsString(install) must include("The request failed HMAC validation")
    }

    "return an internal server error if the expected shopify data could not be parsed" in {
      val controller = app.injector.instanceOf[InstallController]
      val parameters = "timestamp=1557768838"
      val validHmac = ShopifyHmac.calculateHmac(parameters)
      val request = FakeRequest(GET, s"/install?hmac=$validHmac&$parameters")
      val install = controller.install().apply(request)

      status(install) mustBe INTERNAL_SERVER_ERROR
      contentAsString(install) must include("Unable to process the installation request.")
    }
  }
}
