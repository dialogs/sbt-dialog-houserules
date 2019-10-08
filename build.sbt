import ReleaseTransformations._

scalaVersion := "2.12.8"

lazy val commonSettings = Seq(
  organization in ThisBuild := "im.dlg"
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    sbtPlugin := true,
    name := "sbt-dialog-houserules",
    description := "sbt plugin for enforcing sbt house rules in Dialog Team.",
    addSbtPlugin("com.typesafe.sbt"  % "sbt-git"         % "1.0.0"),
    addSbtPlugin("org.foundweekends" % "sbt-bintray"     % "0.5.5"),
    addSbtPlugin("com.jsuereth"      % "sbt-pgp"         % "2.0.0"),
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
