package shopify

import java.io.File

import shopify.generators.{GenerateShopifyConf, GenerationTypes}

object ShopifyAppCli {
  def generate(args: Seq[String], baseDirectory: File): Unit = args match {
    case Seq(GenerationTypes.ShopifyConf, args @ _*) => GenerateShopifyConf(baseDirectory)
    case _ => println(s"Unable to generate for arguments: $args")
  }
}
