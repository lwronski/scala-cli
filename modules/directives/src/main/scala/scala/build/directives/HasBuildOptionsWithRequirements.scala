package scala.build.directives

import scala.build.Ops.*
import scala.build.errors.{BuildException, CompositeBuildException}
import scala.build.options.{BuildOptions, Scope, WithBuildRequirements}

sealed trait HasBuildOptionsWithRequirements {
  def buildOptionsWithRequirements
    : Either[BuildException, List[WithBuildRequirements[BuildOptions]]]
}

trait HasBuildOptionsWithTargetScopeRequirements[T](valuesWithTargetScope: List[(T, Option[Scope])])
    extends HasBuildOptionsWithRequirements {
  def buildOptions(value: T): Either[BuildException, BuildOptions]
  final def buildOptionsWithRequirements
    : Either[BuildException, List[WithBuildRequirements[BuildOptions]]] =
    (for { (value, maybeTargetScope) <- valuesWithTargetScope } yield buildOptions(
      value
    ) -> maybeTargetScope match
      case (Right(bo), Some(scope)) => Right(bo.withScopeRequirement(scope))
      case (Right(bo), None)        => Right(bo.withEmptyRequirements)
      case (Left(e), _)             => Left(e)
    )
      .sequence
      .left.map(CompositeBuildException(_))
      .map(_.toList)
}
