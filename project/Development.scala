import sbt._

object Development {
  val ScalifyProjectFile = file("../scalify/")
  val DevelopmentMode: Boolean = ScalifyProjectFile.exists
  val ProductionMode: Boolean = !DevelopmentMode

  val ScalifyDependsOn: Seq[ClasspathDep[ProjectReference]] =
    if (ProductionMode) Seq.empty
    else Seq (
      ProjectRef(ScalifyProjectFile, "scalify")  % "test->test;compile->compile",
      ProjectRef(ScalifyProjectFile, "scalifyplusplay")  % "test->test;compile->compile"
    )

  val ScalifyLibraryDependencies: Seq[ModuleID] =
    if (DevelopmentMode) Seq.empty
    else Seq(
      Dependencies.Scalify.Core,
      Dependencies.Scalify.PlusPlay,
      Dependencies.Scalify.Core % Test classifier "tests",
      Dependencies.Scalify.PlusPlay % Test classifier "tests"
    )
}
