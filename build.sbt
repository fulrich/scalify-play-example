// Publishing Information
name := """scalify-play-example"""
organization := "com.github.fulrich"
licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.12.8"

// Play Framework Plugins
lazy val root = (project in file(".")).enablePlugins(PlayScala)


// Versions
val V = new {
  val Scalify = "0.0.14"
  val ScalaTest = "4.0.2"
  val TestCharged = "0.1.12"
}

// Resolvers
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("public")
)

// Production Dependencies
libraryDependencies ++= Seq(
  guice,
  caffeine,
  ws,
  "com.github.fulrich" %% "scalify" % V.Scalify,
  "com.github.fulrich" %% "scalifyplus-play" % V.Scalify,
  "com.github.pureconfig" %% "pureconfig" % "0.11.0",
  "io.lemonlabs" %% "scala-uri" % "1.4.5"
)

// Test Dependencies
libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % V.ScalaTest % Test,
  "com.github.fulrich" %% "test-charged" % V.TestCharged % Test,
  "com.github.fulrich" %% "scalify" % V.Scalify % Test classifier "tests",
  "com.github.fulrich" %% "scalifyplus-play" % V.Scalify % Test classifier "tests"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.fredriculrich.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.fredriculrich.binders._"
