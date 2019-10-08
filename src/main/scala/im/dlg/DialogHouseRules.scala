package im.dlg

import sbt._
import Keys._
import bintray.BintrayPlugin
import bintray.BintrayPlugin.autoImport._

import scala.xml.NodeSeq

object DialogHouseRules extends AutoPlugin with Publishing {
  override def requires = plugins.JvmPlugin

  override def trigger = allRequirements

  lazy val defaultDialogSettings = dialogSettings()

  lazy val mitLicense = licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
  lazy val dialogLicense = licenses := Seq("Dialog" -> url("https://dlg.im"))
  lazy val bintrayNoLicense = bintrayOmitLicense := true

  def dialogSettings(
    org: String = "im.dlg",
    publishTo: PublishType = PublishType.PublishToBintray,
    pomExtra: NodeSeq = Nil
  ): Seq[Def.Setting[_]] =
    publishSettings(pomExtra, org, publishTo)
}


trait Publishing {
  sealed trait PublishType

  object PublishType {

    object PublishToBintray extends PublishType

    object PublishToSonatype extends PublishType

  }

  val defaultPublishSettings: Seq[Def.Setting[_]] = publishSettings(Nil, "im.dlg", PublishType.PublishToBintray)

  protected def publishSettings(
    pomExtraVal: NodeSeq,
    org: String,
    publishType: PublishType
  ): Seq[Def.Setting[_]] =
    (publishType match {
      case PublishType.PublishToBintray => BintrayPlugin.bintrayPublishSettings
      case PublishType.PublishToSonatype =>
        Seq(
          publishTo := {
            val nexus = "https://oss.sonatype.org/"
            if (isSnapshot.value)
              Some("snapshots" at nexus + "content/repositories/snapshots")
            else
              Some("releases" at nexus + "service/local/staging/deploy/maven2")
          }
        )
    }) ++
      Seq(
        organization := org,
        bintrayOrganization := Some("dialog"),
        bintrayRepository := "maven",
        pomExtra in Global := pomExtraVal,
        publishMavenStyle := true
      )

  def bintraySettings(repo: String = "maven"): Seq[Def.Setting[_]] = Seq(bintrayRepository := repo)
}

