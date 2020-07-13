package poc.github.codeBlock.filePathResolver

import poc.github.GithubRepo
import utest._

import scala.io.Source

object CodeBlockFilePathResolverFromGithubTest extends TestSuite {
  val fileResolver = new CodeBlockFilePathResolverFromGithub(GithubRepo("lust4life", "coverage-diff-poc"), "f065897")

  val tests = Tests {
    "get raw file from github" - {
      val fileStream = fileResolver.getStream("example/src/poc/example/javacode/TestCaseEntry.java")
      val lines = Source.fromInputStream(fileStream).getLines()
      lines.length ==> 62
    }
  }
}
