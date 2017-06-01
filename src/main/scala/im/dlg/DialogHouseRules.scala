package im.dlg

import bintray.BintrayPlugin
import bintray.BintrayPlugin.autoImport._
import sbt.Keys._
import sbt._
import sbtprotoc.ProtocPlugin.autoImport.PB

import scala.xml.NodeSeq

object DialogHouseRules
    extends AutoPlugin
    with Dependencies
    with Publishing
    with Compiling
    with ScalaPB {
  override def requires = plugins.JvmPlugin

  override def trigger = allRequirements

  lazy val defaultDialogSettings = dialogSettings()

  lazy val mitLicense = licenses := Seq(
    "MIT" -> url("https://opensource.org/licenses/MIT")
  )
  lazy val dialogLicense = licenses := Seq("Dialog" -> url("https://dlg.im"))
  lazy val bintrayNoLicense = bintrayOmitLicense := true

  def dialogSettings(
    org: String = "im.dlg",
    publishTo: PublishType = PublishType.PublishToBintray,
    pomExtra: NodeSeq = Nil
  ): Seq[Def.Setting[_]] =
    publishSettings(pomExtra, org, publishTo) ++ dialogCompileSettings
}

trait Dependencies {
  val scalapbVersion = com.trueaccord.scalapb.compiler.Version.scalapbVersion
  lazy val scalapbDeps: Seq[ModuleID] = Seq(
    "com.trueaccord.scalapb" %% "scalapb-runtime" % scalapbVersion % "protobuf"
  )

  lazy val scalapbGrpcDeps: Seq[ModuleID] = Seq(
    "io.grpc" % "grpc-netty" % "1.5.0",
    "com.trueaccord.scalapb" %% "scalapb-runtime-grpc" % scalapbVersion
  )
}

trait ScalaPB extends Dependencies {
  lazy val scalapbSettings: Seq[Def.Setting[_]] =
    Seq(
      libraryDependencies ++= scalapbDeps,
      PB.targets in Compile := Seq(
        scalapb
          .gen(singleLineToString = true) -> (sourceManaged in Compile).value
      )
    )

  lazy val scalapbGrpcSettings: Seq[Def.Setting[_]] =
    scalapbSettings ++ Seq(
      libraryDependencies ++= scalapbGrpcDeps
    )
}

trait Publishing {
  sealed trait PublishType

  object PublishType {

    object PublishToBintray extends PublishType

    object PublishToSonatype extends PublishType

  }

  val defaultPublishSettings: Seq[Def.Setting[_]] =
    publishSettings(Nil, "im.dlg", PublishType.PublishToBintray)

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

  def bintraySettings(repo: String = "maven"): Seq[Def.Setting[_]] =
    Seq(bintrayRepository := repo)
}

trait Compiling {
  protected lazy val dialogCompileSettings = Seq(
    scalaVersion := "2.12.2",
    //    scalaVersion := "2.11.11",
    scalacOptions in Compile ++= Seq(
      //      "-Ybackend:GenBCode",
      //"-Ydelambdafy:method", // default in 2.12
      //      "-Yopt:l:classpath",
      "-encoding",
      "UTF-8",
      "-deprecation",
      "-unchecked",
      "-feature",
      "-language:higherKinds",
      "-Xfatal-warnings",
      "-Xlint",
      "-Xfuture",
      //"-Ywarn-dead-code",
      "-Ywarn-infer-any",
      "-Ywarn-numeric-widen"
    ),
    javaOptions ++= Seq("-Dfile.encoding=UTF-8"),
    javacOptions ++= Seq(
      "-source",
      "1.8",
      "-target",
      "1.8",
      "-Xlint:unchecked",
      "-Xlint:deprecation"
    )
  )
}
