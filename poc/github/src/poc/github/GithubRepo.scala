package poc.github

final case class GithubRepo(owner: String, repo: String) {
  val prefix = s"https://github.com/$owner/$repo"

  def getCompareCommitDiffUrl(oldVersion: String, newVersion: String) = {
    s"$prefix/compare/$oldVersion...$newVersion.diff"
  }

  def getPullRequestDiffUrl(pullRequestId: Int) = {
    s"$prefix/pull/$pullRequestId.diff"
  }

}

