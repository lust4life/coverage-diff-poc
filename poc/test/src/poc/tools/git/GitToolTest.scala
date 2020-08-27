package poc.tools.git

import utest._

import scala.util.Try

object GitToolTest extends TestSuite {

  val tests: Tests = Tests {

    "ensureRepoCloned should clone from repo url" - {
      val pwd = os.pwd
      val tmp = os.temp.dir()
      val gitTool = GitTool(tmp.toIO, pwd.toString())
      gitTool.ensureRepoCloned()
      assert(os.list(tmp, false).length > 1)
    }

    "ensureRepoCloned should skip if exist dir" - {
      val tmp = os.temp.dir()
      os.makeDir(tmp / ".git")
      val gitTool = GitTool(tmp.toIO, "https://github.com/lust4life/coverage-diff-poc")
      gitTool.ensureRepoCloned()
      os.list(tmp, false).length ==> 1
    }

    "reset to first version should only contains 4 file" - {
      val pwd = os.pwd
      val tmp = os.temp.dir()
      val gitTool = GitTool(tmp.toIO, pwd.toString())
      gitTool.ensureRepoCloned()
      gitTool.resetTo("dfcf215804c508e3a3e2abfec191f18aea9eb39e")
      os.list(tmp, false).length ==> 4
    }

    "reset to some version should failed if not cloned" - {
      val tmp = os.temp.dir()
      val gitTool = GitTool(tmp.toIO, "https://github.com/lust4life/coverage-diff-poc")

      Try {
        gitTool.resetTo("abc")
      }.failed
    }

    "diff should generate some data" - {
      val pwd = os.pwd
      val tmp = os.temp.dir()
      val gitTool = GitTool(tmp.toIO, pwd.toString())
      gitTool.ensureRepoCloned()
      var res = gitTool.diff("master", "master")
      res.out.bytes.length ==> 0

      res = gitTool.diff("master~1", "master")
      assert(res.out.bytes.length > 0)
    }
  }
}
