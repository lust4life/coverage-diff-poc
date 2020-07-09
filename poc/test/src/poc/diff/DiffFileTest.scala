package poc.diff

import utest._

object DiffFileTest extends TestSuite {

  val tests = Tests {

    "created file should have no changed lines" - {
      assert(Created("","").changedLines.isEmpty)
    }

    "deleted file should have no changed lines" - {
      Deleted("","").changedLines.isEmpty ==> true
    }

    "check language is java" - {
      assert(Created("","java").isJava)
      assert(Created("","JAVA").isJava)
      assert(!Created("","scala").isJava)
    }
  }
}
