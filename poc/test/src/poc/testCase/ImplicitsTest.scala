package poc.testCase

import utest._

object ImplicitsTest extends TestSuite {
  val tests = Tests {
    "merge test case" - {
      val testCaseInfos = Seq(
        TestCaseInfo("1", "", Seq(AffectedFile("a", Seq(AffectedMethod("a1"))))),
        TestCaseInfo("1", "", Seq(AffectedFile("b", Seq(AffectedMethod("b1"))))),
        TestCaseInfo("2", "", Seq(AffectedFile("a", Seq(AffectedMethod("c1"))))),
        TestCaseInfo("2", "", Seq(AffectedFile("b", Seq(AffectedMethod("d1"))))),
      )
      val merged = testCaseInfos.merged.toSeq

      merged.length ==> 2
      merged.forall(_.coverage.length == 2) ==> true
    }
  }
}