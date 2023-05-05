package scala.build.preprocessing.directives

import scala.build.directives.*
import scala.build.errors.BuildException
import scala.build.options.{BuildOptions, Scope}
import scala.build.preprocessing.directives.DirectiveUtil.*
import scala.build.{Positioned, options}
import scala.cli.commands.SpecificationLevel
@DirectiveGroupName("Javac options")
@DirectiveExamples("//> using javacOpt -source 1.8 -target 1.8")
@DirectiveExamples("//> using test.javacOpt -source 1.8 -target 1.8")
@DirectiveUsage(
  "//> using javacOpt _options_",
  "`//> using javacOpt `_options_"
)
@DirectiveDescription("Add Javac options which will be passed when compiling sources.")
@DirectiveLevel(SpecificationLevel.SHOULD)
final case class JavacOptions(
  @DirectiveName("javacOpt")
  javacOptions: List[Positioned[String]] = Nil,
  @DirectiveName("test.javacOpt")
  testJavacOptions: List[Positioned[String]] = Nil
) extends HasBuildOptionsWithTargetScopeRequirements(
      List(javacOptions -> None, testJavacOptions -> Some(Scope.Test))
    ) {
  def buildOptions(javacOptions: List[Positioned[String]]): Either[BuildException, BuildOptions] =
    Right(BuildOptions(javaOptions = options.JavaOptions(javacOptions = javacOptions)))
}

object JavacOptions {
  val handler: DirectiveHandler[JavacOptions] = DirectiveHandler.derive
}
