package poc.github.diff.streamLocator

import poc.github.GithubRepo
import utest._

import scala.io.Source

object DiffStreamLocatorFromGithubTest extends TestSuite {
  val diffFromGithub = new DiffStreamLocatorFromGithub(GithubRepo("lust4life", "coverage-diff-poc"))


  val tests = Tests {

    "get diff from github two commit" - {

      val oldCodeVersion = "acec035"
      val newCodeVersion = "acc94da"
      val diffStream = diffFromGithub.getDiffStream(oldCodeVersion, newCodeVersion)
      val diffLines = Source.fromInputStream(diffStream).getLines().toSeq

      assert(diffLines.length > 1)
    }

    "get diff from github pull request" - {
      val pullRequestId = 1
      val diffStream = diffFromGithub.getDiffStreamByPullRequest(pullRequestId)
      val diffLines = Source.fromInputStream(diffStream).getLines().toSeq

      diffLines.length ==> 1
    }
  }
}
