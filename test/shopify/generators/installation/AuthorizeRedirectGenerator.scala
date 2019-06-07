package shopify.generators.installation

import org.scalacheck.Gen
import com.github.fulrich.testcharged.generators._
import shopify.ShopifyConfiguration
import shopify.generators.ShopifyConfigurationGenerator
import shopify.installation.{InstallParameters, AuthorizeRedirect}


object AuthorizeRedirectGenerator {
  def apply(): Gen[AuthorizeRedirect] =
    withConfig(ShopifyConfigurationGenerator().value)

  def withConfig(implicit configuration: ShopifyConfiguration): Gen[AuthorizeRedirect] =
    fromParameters(InstallParametersGenerator().value)

  def fromParameters(parameters: InstallParameters)(implicit configuration: ShopifyConfiguration): Gen[AuthorizeRedirect] =
    for {
      callbackUri <- Generate.alpha
      scopes <- Generate.alpha.tiny.gen.seq
      nonce <- Generate.uuid.map(_.toString)
    } yield AuthorizeRedirect(configuration.apiKey, parameters.shop, callbackUri, configuration.scopes, nonce)
}
