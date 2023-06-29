package scala.cli.javaLauncher

import coursier.Repositories
import coursier.cache.FileCache
import coursier.core.Version
import coursier.util.{Artifact, Task}
import dependency.*

import java.io.File
import scala.build.internal.CsLoggerUtil.CsCacheExtensions
import scala.build.internal.{Constants, OsLibc, Runner}
import scala.build.options.ScalaVersionUtil.fileWithTtl0
import scala.build.options.{BuildOptions, JavaOptions}
import scala.build.{Artifacts, Os, Positioned}
import scala.cli.ScalaCli
import scala.cli.commands.shared.{CoursierOptions, LoggingOptions}
import scala.cli.launcher.LauncherOptions
import scala.concurrent.duration.*
import scala.util.control.NonFatal

object JavaLauncherCli {

  def runAndExit(remainingArgs: Seq[String]): Nothing = {
    val logger          = LoggingOptions().logger
    val scalaCli = System.getProperty("java.class.path").split(File.pathSeparator).iterator.toList.map { f =>
        os.Path(f, os.pwd)
    }

    val buildOptions = BuildOptions(
      javaOptions = JavaOptions(
        jvmIdOpt = Some(OsLibc.baseDefaultJvm(OsLibc.jvmIndexOs, "17")).map(Positioned.none)
      )
    )

    val exitCode =
      Runner.runJvm(
        buildOptions.javaHome().value.javaCommand,
        buildOptions.javaOptions.javaOpts.toSeq.map(_.value.value),
        scalaCli.headOption.toList,
        "coursier.bootstrap.launcher.ResourcesLauncher",
        remainingArgs,
        logger,
        allowExecve = true
      ).waitFor()

    sys.exit(exitCode)
  }

}
