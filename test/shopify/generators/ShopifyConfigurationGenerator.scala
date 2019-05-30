package shopify.generators

import com.github.fulrich.testcharged.generators._
import org.scalacheck.Gen
import shopify.ShopifyConfiguration


object ShopifyConfigurationGenerator {
  def apply(): Gen[ShopifyConfiguration] = for {
    apiKey <- Generate.alpha
    apiSecret <- Generate.alphaNumeric
  } yield ShopifyConfiguration(apiKey, apiSecret)
}
