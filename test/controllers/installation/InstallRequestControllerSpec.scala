package controllers.installation

import com.github.fulrich.scalify.ShopifyConfiguration
import com.github.fulrich.scalify.generators.installation.InstallParametersGenerator
import com.github.fulrich.scalify.hmac.ShopifyHmac
import com.github.fulrich.scalify.installation.InstallParameters
import com.github.fulrich.scalify.play.ShopifyInjectedApplication
import com.github.fulrich.scalify.play.serialization.url.installation.InstallParametersBindable
import com.github.fulrich.testcharged.generators._
import controllers.installation.cache.InstallCache
import org.scalatest.OptionValues
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Mode
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, redirectLocation, status, _}

class InstallRequestControllerSpec
  extends PlaySpec with GuiceOneAppPerTest with ShopifyInjectedApplication with OptionValues {

  "unsafe-install" should {
    "Should redirect to Shopify for authorization if in Development or Test mode" in {
      val parameters = InstallParameters(shop = Generate.alpha.value, timestamp = Generate.instant.soon.value)
      val request = FakeRequest(GET, s"/unsafe-install?shop=${parameters.shop}")
      val stubCache = new FakeCache()
      val controller = customInject[InstallRequestController](_.overrides(bind[InstallCache].toInstance(stubCache)))
      val result = controller.unsafeInstall(parameters.shop).apply(request)

      status(result) mustBe SEE_OTHER
      val redirectUri = redirectLocation(result).value
      stubCache.getNonce(parameters.shop) mustBe defined
      redirectUri must startWith(s"https://${parameters.shop}")
      redirectUri must include(Configuration.scopes.mkString(","))
    }

    "Should return a Forbidden response if the application is in production mode" in {
      val shop = Generate.alpha.value
      val request = FakeRequest(GET, s"/unsafe-install?shop=$shop")
      val result = customInject[InstallRequestController](_.in(Mode.Prod)).unsafeInstall(shop).apply(request)

      status(result) mustBe FORBIDDEN
    }
  }

  "install" should {
    val parameters: InstallParameters = InstallParametersGenerator().value
    val parametersQueryString: String = InstallParametersBindable.unbind(parameters)

    "should redirect to Shopify for authorization if HMAC and parameters are valid" in {
      val validHmac = ShopifyHmac.calculateHmac(parametersQueryString)(Configuration)
      val request = FakeRequest(GET, s"/install?hmac=$validHmac&$parametersQueryString")
      val stubCache = new FakeCache()
      val controller = customInject[InstallRequestController](_.overrides(bind[InstallCache].toInstance(stubCache)))
      val result = controller.install().apply(request)

      status(result) mustBe SEE_OTHER
      val redirectUri = redirectLocation(result).value
      stubCache.getNonce(parameters.shop) mustBe defined
      redirectUri must startWith (s"https://${parameters.shop}")
      redirectUri must include (Configuration.scopes.mkString(","))
    }

    "return an internal server error if the HMAC is not valid" in {
      val invalidHmac = ShopifyHmac.calculateHmac("wrong_parameters")(Configuration)
      val request = FakeRequest(GET, s"/install?hmac=$invalidHmac&$parameters")
      val install = inject[InstallRequestController].install().apply(request)

      status(install) mustBe FORBIDDEN
      contentType(install) mustBe Some("text/plain")
    }
  }
}
