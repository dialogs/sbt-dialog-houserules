import ReleaseTransformations._

scalaVersion := "2.10.6"

lazy val commonSettings = Seq(
  organization in ThisBuild := "im.dlg"
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    sbtPlugin := true,
    name := "sbt-dialog-houserules",
    description := "sbt plugin for enforcing sbt house rules in Dialog Team.",
    //addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "0.6.8"),
    addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "0.3"),
    addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0"),
    addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.3"),
    addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0"),
    addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.8"),
    libraryDependencies += "com.trueaccord.scalapb" %% "compilerplugin" % "0.6.0-SNAPSHOT",
    licenses := Seq("Dialog" -> url("https://dlg.im")),
    publishMavenStyle := false,
    bintrayRepository := "sbt-plugins",
    bintrayOrganization := Some("dialog"),
    releaseProcess := Seq(
      checkSnapshotDependencies,
      inquireVersions,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      publishArtifacts,
      setNextVersion,
      commitNextVersion,
      pushChanges
    )
  )
