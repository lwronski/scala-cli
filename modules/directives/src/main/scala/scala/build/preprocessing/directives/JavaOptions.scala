package scala.build.preprocessing.directives

import scala.build.directives.*
import scala.build.errors.BuildException
import scala.build.options.{BuildOptions, JavaOpt, Scope, ShadowingSeq}
import scala.build.{Positioned, options}
import scala.cli.commands.SpecificationLevel

@DirectiveGroupName("Java options")
@DirectiveExamples("//> using javaOpt -Xmx2g, -Dsomething=a")
@DirectiveExamples("//> using test.javaOpt -Dsomething=a")
@DirectiveUsage(
  "//> using javaOpt _options_",
  "`//> using javaOpt `_options_"
)
@DirectiveDescription("Add Java options which will be passed when running an application.")
@DirectiveLevel(SpecificationLevel.MUST)
final case class JavaOptions(
  @DirectiveName("javaOpt")
  javaOptions: List[Positioned[String]] = Nil,
  @DirectiveName("test.javaOpt")
  testJavaOptions: List[Positioned[String]] = Nil
) extends HasBuildOptionsWithTargetScopeRequirements(
      List(javaOptions -> None, testJavaOptions -> Some(Scope.Test))
    ) {
  def buildOptions(javaOptions: List[Positioned[String]]): Either[BuildException, BuildOptions] =
    Right {
      BuildOptions(javaOptions =
        options.JavaOptions(javaOpts = ShadowingSeq.from(javaOptions.map(_.map(JavaOpt(_)))))
      )
    }
}

object JavaOptions {
  val handler: DirectiveHandler[JavaOptions] = DirectiveHandler.derive
}
