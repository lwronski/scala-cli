package scala.build.preprocessing.directives

import dependency.AnyDependency

import scala.build.EitherCps.{either, value}
import scala.build.Positioned
import scala.build.directives.*
import scala.build.errors.BuildException
import scala.build.options.{BuildOptions, ClassPathOptions, Scope, ShadowingSeq}
import scala.build.preprocessing.directives.DirectiveUtil.*
import scala.cli.commands.SpecificationLevel

@DirectiveExamples("//> using dep com.lihaoyi::os-lib:0.9.1")
@DirectiveExamples("//> using test.dep org.scalatest::scalatest:3.2.10")
@DirectiveExamples("//> using test.dep org.scalameta::munit:0.7.29")
@DirectiveExamples(
  "//> using dep tabby:tabby:0.2.3,url=https://github.com/bjornregnell/tabby/releases/download/v0.2.3/tabby_3-0.2.3.jar"
)
@DirectiveUsage(
  "//> using dep org:name:ver | //> using deps org:name:ver org2:name2:ver2",
  "`//> using dep `_org_`:`name`:`ver"
)
@DirectiveDescription("Add dependencies")
@DirectiveLevel(SpecificationLevel.MUST)
final case class Dependency(
  @DirectiveName("lib")  // backwards compat
  @DirectiveName("libs") // backwards compat
  @DirectiveName("dep")
  @DirectiveName("deps")
  @DirectiveName("dependencies")
  dependency: List[Positioned[String]] = Nil,
  @DirectiveName("test.dep")
  @DirectiveName("test.deps")
  @DirectiveName("test.dependencies")
  testDependency: List[Positioned[String]] = Nil
) extends HasBuildOptionsWithTargetScopeRequirements(
      List(
        dependency     -> None,
        testDependency -> Some(Scope.Test)
      )
    ) {
  def buildOptions(ds: List[Positioned[String]]): Either[BuildException, BuildOptions] = either {
    val dependencies: ShadowingSeq[Positioned[AnyDependency]] =
      value(ds.asDependencies.map(ShadowingSeq.from))
    BuildOptions(classPathOptions = ClassPathOptions(extraDependencies = dependencies))
  }
}

object Dependency {
  val handler: DirectiveHandler[Dependency] = DirectiveHandler.derive
}
