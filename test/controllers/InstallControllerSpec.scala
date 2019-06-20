package controllers

import com.github.fulrich.scalify.generators.installation.InstallParametersGenerator
import com.github.fulrich.scalify.hmac.ShopifyHmac
import com.github.fulrich.scalify.installation.InstallParameters
import com.github.fulrich.scalify.play.ShopifyInjectedApplication
import com.github.fulrich.scalify.play.serialization.url.installation.InstallParametersBindable
import com.github.fulrich.testcharged.generators._
import org.scalatest.OptionValues
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Helpers._
import play.api.test._


class InstallControllerSpec extends PlaySpec with GuiceOneAppPerTest with ShopifyInjectedApplication with OptionValues {
  val parameters: InstallParameters = InstallParametersGenerator().value
  val parametersQueryString: String = InstallParametersBindable.unbind(parameters)
  def controller: InstallController = inject[InstallController]

  "InstallController GET" should {
    "output the parsed parameters if HMAC is valid" in {
      val validHmac = ShopifyHmac.calculateHmac(parametersQueryString)(Configuration)
      val request = FakeRequest(GET, s"/install?hmac=$validHmac&$parametersQueryString")
      val result = controller.install().apply(request)

      status(result) mustBe SEE_OTHER
      val redirectUri = redirectLocation(result).value
      redirectUri must startWith (s"https://${parameters.shop}")
      redirectUri must include (Configuration.scopes.mkString(","))
    }

    "return an internal server error if the HMAC is not valid" in {
      val invalidHmac = ShopifyHmac.calculateHmac("wrong_parameters")(Configuration)
      val request = FakeRequest(GET, s"/install?hmac=$invalidHmac&$parameters")
      val install = controller.install().apply(request)

      status(install) mustBe FORBIDDEN
      contentType(install) mustBe Some("text/plain")
    }
  }
}
