package poc.example

import utest._

object ShowcaseTest extends TestSuite {
  val tests = Tests {
    "showCoverageChangedInfo" - {
      Showcase.runTestCase(1)

      val infos = Showcase.showCoverageChangedInfo(4)

      assert(infos != "[]")
    }
  }
}
