package im.dlg

import sbt._
import Keys._
import org.scalafmt.sbt.ScalafmtPlugin
import sbt.{ IntegrationTest => It }

object FormatPlugin extends AutoPlugin {
  override def requires = plugins.JvmPlugin && ScalafmtPlugin
  override def trigger = allRequirements

  object autoImport {
    lazy val scalafmtIncremental: TaskKey[Unit] =
      taskKey[Unit]("Reformat on compile")
    lazy val scalafmtReformatOnCompile: Seq[Def.Setting[_]] = Seq(
      compileInputs in (Compile, compile) := {
        (compileInputs in (Compile, compile))
          .dependsOn(scalafmtIncremental in Compile)
          .value
      },
      compileInputs in (Test, compile) := {
        (compileInputs in (Test, compile))
          .dependsOn(scalafmtIncremental in Test)
          .value
      }
    )
    lazy val reformatOnCompileWithItSettings: Seq[Def.Setting[_]] =
      scalafmtReformatOnCompile ++ List(
        compileInputs in (It, compile) := {
          (compileInputs in (It, compile))
            .dependsOn(scalafmtIncremental in It)
            .value
        }
      )
  }
  import autoImport._

  def incrementalReformatSettings: Seq[Def.Setting[_]] = Seq(
    (sourceDirectories in scalafmtIncremental) := unmanagedSourceDirectories.value,
    includeFilter in scalafmtIncremental := "*.scala",
    scalafmtIncremental := Def.taskDyn {
      val cache = streams.value.cacheDirectory / "scalafmt"
      val include = (includeFilter in scalafmtIncremental).value
      val exclude = (excludeFilter in scalafmtIncremental).value
      val files: Set[File] =
        (sourceDirectories in scalafmtIncremental).value
          .descendantsExcept(include, exclude)
          .get
          .toSet
      val label = Reference.display(thisProjectRef.value)
      def handleUpdate(
        in: ChangeReport[File],
        out: ChangeReport[File]
      ): Set[File] = {
        val files = in.modified -- in.removed
        import sbt._
        inc.Analysis
          .counted("Scala source", "", "s", files.size)
          .foreach(count =>
            streams.value.log.info(s"Formatting $count $label..."))
        files
      }
      val toFormat = FileFunction.cached(cache)(
        FilesInfo.hash,
        FilesInfo.exists
      )(handleUpdate)(files)
      val filesFlag = toFormat.map(_.getAbsolutePath).mkString(",")
      val args = Seq("", "org.scalafmt.cli.Cli", "-i", "-f", filesFlag)
      (runMain in ScalafmtPlugin.scalafmtStub).toTask(args.mkString(" "))
    }.value
  )

  override lazy val projectSettings: Seq[Def.Setting[_]] =
    inConfig(Compile)(incrementalReformatSettings) ++
      inConfig(Test)(incrementalReformatSettings)
}
