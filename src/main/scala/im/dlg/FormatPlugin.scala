package im.dlg

import sbt._
import Keys._
import com.typesafe.sbt.SbtScalariform
import SbtScalariform.{ ScalariformKeys => sr, _ }

object FormatPlugin extends AutoPlugin {
  override def requires = plugins.JvmPlugin && SbtScalariform
  override def trigger = allRequirements

  override lazy val projectSettings: Seq[Def.Setting[_]] = baseSettings

  lazy val baseSettings = Seq(
    sr.preferences := formattingPreferences
  )

  private def formattingPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences()
      .setPreference(RewriteArrowSymbols, true)
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
  }
}