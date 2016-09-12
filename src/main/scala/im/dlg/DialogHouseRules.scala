package im.dlg

import bintray.BintrayPlugin
import bintray.BintrayPlugin.autoImport._
import com.trueaccord.scalapb.{ ScalaPbPlugin => PB }
import sbt.Keys._
import sbt._

import scala.xml.NodeSeq

object DialogHouseRules extends AutoPlugin with Dependencies with Publishing with Compiling with ScalaPB {
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
    publishSettings(pomExtra, org, publishTo) ++ dialogCompileSettings
}

trait Dependencies {
  lazy val protobufDeps: Seq[Def.Setting[_]] = Seq(
    libraryDependencies += "com.google.protobuf" % "protobuf-java" % "3.0.0-beta-4",
    dependencyOverrides ~= { overrides =>
      overrides + "com.google.protobuf" % "protobuf-java" % "3.0.0-beta-4"
    }
  )

  lazy val scalapbDeps: Seq[Def.Setting[_]] = Seq(
    libraryDependencies += "com.trueaccord.scalapb" %% "scalapb-runtime" % "0.5.34" % PB.protobufConfig
  ) ++ protobufDeps

  lazy val grpcDeps: Seq[Def.Setting[_]] = Seq(
    libraryDependencies += "io.grpc" % "grpc-netty" % "0.15.0"
  )

  lazy val scalapbGrpcDeps: Seq[Def.Setting[_]] = Seq(
    libraryDependencies += "com.trueaccord.scalapb" %% "scalapb-runtime-grpc" % (PB.scalapbVersion in PB.protobufConfig).value
  ) ++ grpcDeps
}

trait ScalaPB extends Dependencies {
  import com.trueaccord.scalapb.{ ScalaPbPlugin => PB }

  lazy val scalapbSettings: Seq[Def.Setting[_]] =
    scalapbDeps ++
      PB.protobufSettings ++ Seq(
        PB.runProtoc in PB.protobufConfig := (args => com.github.os72.protocjar.Protoc.runProtoc("-v300" +: args.toArray)),
        scalaSource in PB.protobufConfig := (sourceManaged in Compile).value.getParentFile / "scalapb"
      )

  lazy val scalapbGrpcSettings: Seq[Def.Setting[_]] =
    scalapbSettings ++ scalapbGrpcDeps
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

trait Compiling {
  protected lazy val dialogCompileSettings = Seq(
    scalaVersion := "2.11.8",
    scalacOptions in Compile ++= Seq(
      "-target:jvm-1.8",
      "-Ybackend:GenBCode",
      "-Ydelambdafy:method",
      "-Yopt:l:classpath",
      "-encoding", "UTF-8",
      "-deprecation",
      "-unchecked",
      "-feature",
      "-language:higherKinds",
      "-Xfatal-warnings",
      "-Xlint",
      "-Xfuture",
      "-Ywarn-dead-code",
      "-Ywarn-infer-any",
      "-Ywarn-numeric-widen"
    ),
    javaOptions ++= Seq("-Dfile.encoding=UTF-8"),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint:unchecked", "-Xlint:deprecation")
  )
}