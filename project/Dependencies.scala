import sbt._

// Versions
object Dependencies {
  val Scalify = new {
    val Version = "0.0.21"
    val Core = "com.github.fulrich" %% "scalify" % Version
    val PlusPlay = "com.github.fulrich" %% "scalifyplus-play" % Version
  }

  val ScalaTest = new {
    val Version = "4.0.3"
    val PlusPlay = "org.scalatestplus.play" %% "scalatestplus-play" % Version
  }

  val TestCharged = new {
    val Version = "0.1.16"
    val Core = "com.github.fulrich" %% "test-charged" % Version
  }

  val PureConfig = new {
    val Version = "0.11.0"
    val Core = "com.github.pureconfig" %% "pureconfig" % Version
  }

  val ScalaUri = new {
    val Version = "1.5.1"
    val Core = "io.lemonlabs" %% "scala-uri" % Version
  }
}
