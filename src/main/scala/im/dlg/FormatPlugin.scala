package im.dlg

import sbt._
import org.scalafmt.sbt.ScalaFmtPlugin

object FormatPlugin extends AutoPlugin {
  override def requires = plugins.JvmPlugin && ScalaFmtPlugin
  override def trigger = allRequirements

  override lazy val projectSettings: Seq[Def.Setting[_]] = baseSettings

  lazy val baseSettings = Seq.empty[Def.Setting[_]]
}