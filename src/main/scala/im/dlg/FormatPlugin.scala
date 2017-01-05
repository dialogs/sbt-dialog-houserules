package im.dlg

import sbt._
import org.scalafmt.sbt.ScalaFmtPlugin
import org.scalafmt.sbt.ScalaFmtPlugin.autoImport._
import sbt.Keys.baseDirectory

object FormatPlugin extends AutoPlugin {
  override def requires = plugins.JvmPlugin && ScalaFmtPlugin
  override def trigger = allRequirements

  override lazy val projectSettings: Seq[Def.Setting[_]] = baseSettings

  lazy val baseSettings = Seq(
    scalafmtConfig := Some(baseDirectory.in(ThisBuild).value / ".scalafmt.conf")
  ) ++ reformatOnCompileSettings
}