// Publishing Information
inThisBuild(List(
  name := """scalify-play-example""",
  organization := "com.github.fulrich",
  homepage := Some(url("https://github.com/fulrich/scalify")),
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
    Developer(
      "fulrich",
      "Fredric Ulrich",
      "9284621+fulrich@users.noreply.github.com",
      url("https://www.fredriculrich.com")
    )
  ),
  version := "1.0.0-SNAPSHOT",
  scalaVersion := "2.12.8",
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("public")
  )
))

// Play Framework Plugins
lazy val root = (project in file(".")).enablePlugins(PlayScala)

// Production Dependencies
libraryDependencies ++= Seq(
  guice,
  caffeine,
  ws,
  "com.github.fulrich" %% "scalify" % "0.0.9",
  "com.github.fulrich" %% "scalifyplus-play" % "0.0.9",
  "com.github.pureconfig" %% "pureconfig" % "0.11.0",
  "io.lemonlabs" %% "scala-uri" % "1.4.5"
)

// Test Dependencies
libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.2" % Test,
  "com.github.fulrich" %% "test-charged" % "0.1.12" % Test,
  "com.github.fulrich" %% "scalify" % "0.0.9" % Test classifier "tests"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.fredriculrich.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.fredriculrich.binders._"
