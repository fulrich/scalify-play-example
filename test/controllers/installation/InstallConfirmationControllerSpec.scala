package controllers.installation

import com.github.fulrich.scalify.hmac.ShopifyHmac
import com.github.fulrich.scalify.play.ShopifyInjectedApplication
import org.scalatest.OptionValues
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, redirectLocation, status}

class InstallConfirmationControllerSpec
  extends PlaySpec with GuiceOneAppPerTest with ShopifyInjectedApplication with OptionValues {


}
