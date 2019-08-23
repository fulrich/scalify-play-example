import sbt._

object Scalify {
  val ProjectFile = file("../scalify/")
  val DevelopmentMode: Boolean = ProjectFile.exists
  val ProductionMode: Boolean = !DevelopmentMode

  val DependsOn: Seq[ClasspathDep[ProjectReference]] =
    if (ProductionMode) Seq.empty
    else Seq (
      ProjectRef(ProjectFile, "core")  % "test->test;compile->compile",
      ProjectRef(ProjectFile, "plusplay")  % "test->test;compile->compile"
    )

  val LibraryDependencies: Seq[ModuleID] =
    if (DevelopmentMode) Seq.empty
    else Seq(
      Dependencies.Scalify.Core,
      Dependencies.Scalify.PlusPlay,
      Dependencies.Scalify.Core % Test classifier "tests",
      Dependencies.Scalify.PlusPlay % Test classifier "tests"
    )
}
