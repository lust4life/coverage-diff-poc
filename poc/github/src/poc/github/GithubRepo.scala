package poc.github

final case class GithubRepo(owner: String, repo: String) {
  def getRawFileUrl(commit: String, filePath: String): String = {
    s"https://raw.githubusercontent.com/$owner/$repo/$commit/$filePath"
  }

  val prefix = s"https://github.com/$owner/$repo"

  def getCompareCommitDiffUrl(oldVersion: String, newVersion: String) = {
    s"$prefix/compare/$oldVersion...$newVersion.diff"
  }

  def getPullRequestDiffUrl(pullRequestId: Int) = {
    s"$prefix/pull/$pullRequestId.diff"
  }

}

