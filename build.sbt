// Publishing Information
name := """scalify-play-example"""
organization := "com.github.fulrich"
licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.13.0"

// Resolvers
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("public")
)

// Setup App Settings
lazy val root = (project in file("."))
  .dependsOn(Scalify.DependsOn: _*)
  .enablePlugins(PlayScala)

// Production Dependencies
libraryDependencies ++= Scalify.LibraryDependencies

libraryDependencies ++= Seq(
  guice,
  caffeine,
  ws,
  Dependencies.PureConfig.Core,
  Dependencies.ScalaUri.Core
)

// Test Dependencies
libraryDependencies ++= Seq(
  Dependencies.ScalaTest.PlusPlay % Test,
  Dependencies.TestCharged.Core % Test,
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.fredriculrich.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.fredriculrich.binders._"
