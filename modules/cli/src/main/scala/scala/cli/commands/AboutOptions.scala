package scala.cli.commands

import caseapp._

@HelpMessage("Print details about this application")
final case class AboutOptions(
  @Group("About")
  @Name("v")
  @HelpMessage("Print only the scala-cli version")
    version: Boolean = false
)

object AboutOptions {
  implicit val parser = Parser[AboutOptions]
  implicit val help = Help[AboutOptions]
}
