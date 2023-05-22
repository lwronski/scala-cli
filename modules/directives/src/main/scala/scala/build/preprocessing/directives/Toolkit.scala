package scala.build.preprocessing.directives

import dependency.*

import scala.build.Positioned
import scala.build.directives.*
import scala.build.errors.BuildException
import scala.build.internal.Constants
import scala.build.options.BuildRequirements.ScopeRequirement
import scala.build.options.ScalaVersionUtil.asVersion
import scala.build.options.WithBuildRequirements.*
import scala.build.options.{
  BuildOptions,
  BuildRequirements,
  ClassPathOptions,
  Scope,
  ShadowingSeq,
  WithBuildRequirements
}
import scala.cli.commands.SpecificationLevel

@DirectiveGroupName("Toolkit")
@DirectiveExamples("//> using toolkit 0.1.0")
@DirectiveExamples("//> using toolkit latest")
@DirectiveExamples("//> using test.toolkit latest")
@DirectiveUsage(
  "//> using toolkit _version_",
  "`//> using toolkit` _version_"
)
@DirectiveDescription("Use a toolkit as dependency")
@DirectiveLevel(SpecificationLevel.SHOULD)
final case class Toolkit(
  toolkit: Option[Positioned[String]] = None,
  @DirectiveName("test.toolkit")
  testToolkit: Option[Positioned[String]] = None
) extends HasBuildOptionsWithRequirements {
  def buildOptionsList: List[Either[BuildException, WithBuildRequirements[BuildOptions]]] =
    Toolkit.resolveDependenciesWithWithRequirements(toolkit) ++
      Toolkit.resolveDependenciesWithWithRequirements(testToolkit)

}

object Toolkit {
  def resolveDependencies(toolkitCoords: Positioned[String]): (
    Positioned[DependencyLike[NameAttributes, NameAttributes]],
    Option[Positioned[DependencyLike[NameAttributes, NameAttributes]]]
  ) =
    toolkitCoords match
      case Positioned(positions, coords) =>
        val tokens  = coords.split(':')
        val version = tokens.last
        val v       = if version == "latest" then "latest.release" else version
        val flavor  = tokens.dropRight(1).headOption
        val org = flavor match {
          case Some("typelevel") => Constants.typelevelOrganization
          case Some(org)         => org
          case None              => Constants.toolkitOrganization
        }
        if org == Constants.toolkitOrganization && (version == "latest" || v.asVersion > "0.1.6".asVersion)
        then
          (
            Positioned(positions, dep"$org::${Constants.toolkitName}::$v,toolkit"),
            Some(Positioned(positions, dep"$org::${Constants.toolkitTestName}::$v,toolkit"))
          )
        else
          (
            Positioned(positions, dep"$org::${Constants.toolkitName}::$v,toolkit"),
            None
          )
  val handler: DirectiveHandler[Toolkit] = DirectiveHandler.derive
  private def resolveDependenciesWithWithRequirements(toolkitOpt: Option[Positioned[String]])
    : List[Either[BuildException, WithBuildRequirements[BuildOptions]]] =
    toolkitOpt match {
      case Some(toolkit) =>
        val (toolkitDep, toolkitTestDepOpt) = Toolkit.resolveDependencies(toolkit)
        List(Right(Toolkit.buildOptions(toolkitDep).withEmptyRequirements)) ++
          toolkitTestDepOpt.toList.map(dep =>
            Right(Toolkit.buildOptions(dep).withScopeRequirement(Scope.Test))
          )
      case None => Nil
    }
  private def buildOptions(
    toolkitDep: Positioned[AnyDependency]
  ): BuildOptions =
    BuildOptions(
      classPathOptions = ClassPathOptions(
        extraDependencies = ShadowingSeq.from(Seq(toolkitDep))
      )
    )

}
