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
      val fileCount = os.list(tmp).length

      os.write(tmp / "hello", "world")
      os.list(tmp).length ==> fileCount + 1

      gitTool.resetTo("master")
      os.list(tmp).length ==> fileCount
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

      os.write.append(tmp / "README.md", "add some info")
      res = gitTool.diff("master", ".")
      assert(res.out.bytes.length > 0)
    }
  }
}
