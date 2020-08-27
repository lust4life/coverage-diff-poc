package poc.tools.git

import java.io.File

import os.CommandResult

final case class GitTool(repoDir: File, repoUrl: String) {
  private val sourceCodeDirPath = os.Path(repoDir)

  def diff(oldVersion: String, newVersion: String): CommandResult = {
    val res =
      os.proc("git", "diff", "-U0", "--no-color", oldVersion, newVersion)
        .call(cwd = sourceCodeDirPath)
    res
  }

  def ensureRepoCloned(): Unit = {
    if (!os.exists(sourceCodeDirPath / ".git")) {
      os.makeDir.all(sourceCodeDirPath)
      os.proc("git", "clone", "-q", repoUrl, ".")
        .call(cwd = sourceCodeDirPath)
    }
  }

  def resetTo(version: String): Unit = {
    os.proc("git", "clean", "-dfq").call(cwd = sourceCodeDirPath)
    os.proc("git", "reset", "--hard", version)
      .call(cwd = sourceCodeDirPath)
  }
}
