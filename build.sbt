name := """shopify-scala"""
organization := "com.fredriculrich"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

// Production Dependencies
libraryDependencies ++= Seq(
  guice,
  caffeine,
  "com.github.pureconfig" %% "pureconfig" % "0.11.0",
  "io.lemonlabs" %% "scala-uri" % "1.4.5"
)

// Test Dependencies
libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.2" % Test
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.fredriculrich.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.fredriculrich.binders._"
