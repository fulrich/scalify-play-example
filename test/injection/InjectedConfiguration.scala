package injection

import com.github.fulrich.scalify.ShopifyConfiguration
import com.github.fulrich.scalify.generators.ShopifyConfigurationGenerator
import org.scalatest.TestSuite
import org.scalatestplus.play.FakeApplicationFactory
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{HasApp, Injecting}
import com.github.fulrich.testcharged.generators._


trait InjectedConfiguration extends FakeApplicationFactory with Injecting { self: TestSuite with HasApp =>
  implicit val Configuration: ShopifyConfiguration = ShopifyConfigurationGenerator().value

  override def fakeApplication(): Application =
    GuiceApplicationBuilder().overrides(bind[ShopifyConfiguration].toInstance(Configuration)).build()
}
