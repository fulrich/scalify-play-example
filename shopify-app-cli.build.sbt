import complete.DefaultParsers._

lazy val generate = inputKey[Unit]("Generate Shopify specific code and configuration.")

generate := {
  val args: Seq[String] = spaceDelimited("<arg>").parsed
  shopify.ShopifyAppCli.generate(args, (baseDirectory in Compile).value)
}
