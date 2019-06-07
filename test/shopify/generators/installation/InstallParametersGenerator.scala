package shopify.generators.installation

import com.github.fulrich.testcharged.generators._
import org.scalacheck.Gen
import shopify.installation.InstallParameters


object InstallParametersGenerator {
  def apply(): Gen[InstallParameters] = for {
    shop <- Generate.alpha
    timestamp <- Generate.alpha
  } yield InstallParameters(shop, timestamp)
}
