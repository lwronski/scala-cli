package scala.build.actionable
import scala.build.Positioned
import scala.build.options.BuildOptions
import scala.build.errors.BuildException

trait ActionableHandler[V] {
  def extractValues(options: BuildOptions): Seq[Positioned[V]]
  def createActionableDiagnostic(
    option: Positioned[V],
    buildOptions: BuildOptions
  ): Either[BuildException, ActionableDiagnostic]
}
