package scala.build.directives

import scala.build.Ops.*
import scala.build.errors.{BuildException, CompositeBuildException}
import scala.build.options.{BuildOptions, Scope, WithBuildRequirements}

sealed trait HasBuildOptionsWithRequirements {
  def buildOptionsWithRequirements
    : Either[BuildException, List[WithBuildRequirements[BuildOptions]]]
}

trait HasBuildOptionsWithTargetScopeRequirements
    extends HasBuildOptionsWithRequirements {
  def buildOptions: List[Either[BuildException,WithBuildRequirements[BuildOptions]]]
  final def buildOptionsWithRequirements
    : Either[BuildException, List[WithBuildRequirements[BuildOptions]]] =
    buildOptions
      .sequence
      .left.map(CompositeBuildException(_))
      .map(_.toList)
}
