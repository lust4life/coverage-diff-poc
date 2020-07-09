package poc.diff

import utest._

object JavaDiffFilterTest extends TestSuite {
  val tests = Tests {

    "should filter java diff" - {
      JavaDiffFilter.filter(Created("","java")) ==> true
      JavaDiffFilter.filter(Created("","scala")) ==> false
    }
  }
}
