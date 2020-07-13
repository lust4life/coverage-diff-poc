package poc.github
package diff.streamLocator

import java.io.{ByteArrayInputStream, InputStream}

import poc.diff.DiffStreamLocator


class DiffStreamLocatorFromGithub(repo: GithubRepo) extends DiffStreamLocator {
  /**
   * get diff stream from two source code version
   *
   * @param oldVersion old source code version
   * @param newVersion new source code version
   * @return
   */
  override def getDiffStream(oldVersion: String, newVersion: String): InputStream = {
    val response = requests.get(repo.getCompareCommitDiffUrl(oldVersion, newVersion))
    new ByteArrayInputStream(response.bytes)
  }

  def getDiffStreamByPullRequest(pullRequestId: Int): InputStream = {
    val response = requests.get(repo.getPullRequestDiffUrl(pullRequestId))
    new ByteArrayInputStream(response.bytes)
  }
}

