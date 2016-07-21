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
    libraryDependencies += "com.trueaccord.scalapb" %% "scalapb-runtime" % "0.5.34"
  ) ++ protobufDeps
}

trait ScalaPB extends Dependencies {
  import com.trueaccord.scalapb.{ ScalaPbPlugin => PB }

  lazy val scalapbSettings: Seq[Def.Setting[_]] =
    scalapbDeps ++
      PB.protobufSettings :+
      (PB.runProtoc in PB.protobufConfig := (args => com.github.os72.protocjar.Protoc.runProtoc("-v300" +: args.toArray)))
}

trait Publishing {
  sealed trait PublishType

  object PublishType {

    object PublishToBintray extends PublishType

    object PublishToSonatype extends PublishType

  }

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
        pomExtra in Global := pomExtraVal
      )
}

trait Compiling {
  protected lazy val dialogCompileSettings = Seq(
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