package poc.diff.streamLocator

import java.io.InputStream

import poc.diff.DiffStreamLocator
import poc.tools.git.GitTool

class DiffStreamLocatorFromGit(gitTool: GitTool) extends DiffStreamLocator {
  /**
   * get diff stream from two source code version
   *
   * @param oldVersion old source code version
   * @param newVersion new source code version
   * @return
   */
  override def getDiffStream(oldVersion: String, newVersion: String): InputStream = {
    gitTool.diff(oldVersion, newVersion)
  }
}
