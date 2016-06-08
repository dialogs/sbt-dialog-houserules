import ReleaseTransformations._

lazy val commonSettings = Seq(
  organization in ThisBuild := "im.dlg"
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    sbtPlugin := true,
    name := "sbt-dialog-houserules",
    description := "sbt plugin for enforcing sbt house rules in dialog Team.",
    addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.5.1"),
    addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0"),
    licenses := Seq("MIT" -> url("https://github.com/dialogs/sbt-dialog-houserules/blob/master/LICENSE")),
    scmInfo := Some(ScmInfo(url("https://github.com/dialogs/sbt-dialog-houserules"), "git@github.com:dialogs/sbt-dialog-houserules.git")),
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
