package scala.build.internal.util

object WarningMessages {
  private val scalaCliGithubUrl = "https://github.com/VirtusLab/scala-cli"
  def experimentalFeatureUsed(featureName: String): String =
    s"""$featureName is an experimental feature.
       |Please bear in mind that non-ideal user experience should be expected.
       |If you encounter any bugs or have feedback to share, make sure to reach out to the maintenance team at $scalaCliGithubUrl""".stripMargin
  def experimentalDirectiveUsed(name: String): String =
    experimentalFeatureUsed(s"The '$name' directive")

  def experimentalSubcommandUsed(name: String): String =
    experimentalFeatureUsed(s"The '$name' sub-command")

  def experimentalOptionUsed(name: String): String =
    experimentalFeatureUsed(s"The '$name' option")
}
