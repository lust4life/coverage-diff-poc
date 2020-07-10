package poc.testCase

import poc.codeBlock.{BlockRange, ClassOrInterface, Method}
import poc.diff.{Changed, Created}
import utest._

object TestCaseInfoTest extends TestSuite {
  val tests = Tests {
    "test case info plus with different id" - {
      val t1 = TestCaseInfo("1", "", Seq(AffectedFile("a", Seq(AffectedMethod("a1")))))
      val t2 = TestCaseInfo("2", "", Seq(AffectedFile("a", Seq(AffectedMethod("a1")))))

      val res = t1 + t2
      res ==> t1
    }

    "test case info should merge file and methods" - {
      val a1 = AffectedMethod("a1")
      val a2 = AffectedMethod("a2")
      val b1 = AffectedMethod("a1")
      val b2 = AffectedMethod("a2")
      val t1 = TestCaseInfo("1", "", Seq(AffectedFile("a", Seq(a1))))
      val t2 = TestCaseInfo("1", "", Seq(AffectedFile("a", Seq(a2))))
      val t3 = TestCaseInfo("1", "", Seq(AffectedFile("b", Seq(b1))))
      val t4 = TestCaseInfo("1", "", Seq(AffectedFile("b", Seq(b2))))

      val res = t1 + t2 + t3 + t4
      res ==> TestCaseInfo("1", "", Seq(AffectedFile("a", Seq(a1, a2)), AffectedFile("b", Seq(b1, b2))))
    }

    "affected file plus with different path" - {
      val a1 = AffectedMethod("a1")
      val f1 = AffectedFile("a", Seq(a1))
      val f2 = AffectedFile("b", Seq(a1))

      val res = f1 + f2
      res ==> f1
    }

    "affected file should merge methods with same path" - {
      val a1 = AffectedMethod("a1")
      val a2 = AffectedMethod("a2")
      val f1 = AffectedFile("a", Seq(a1))
      val f2 = AffectedFile("a", Seq(a2))

      val res = f1 + f2
      res ==> AffectedFile("a", Seq(a1, a2))

      val f3 = res + f1
      // should distinct method, so only a1, a2 not a1, a2, a1
      f3 ==> AffectedFile("a", Seq(a1, a2))
    }

    "affected method check changed by diff file and code block" - {
      val a1 = AffectedMethod("a.b.c\t#\tDoSomething(Int,String)")
      val classCodeBlock = ClassOrInterface("some.file", BlockRange(1, 30), "a.b.c")
      val methodCodeBlock = Method(classCodeBlock, "DoSomething(Int,String)", BlockRange(15, 17))


      // method not match means missing in the code blocks, should consider be changed
      AffectedMethod("method not match").isChangedBy(Changed("", "", Set(16)), Seq(methodCodeBlock)) ==> true


      // 1,2,3,4 not between 15 - 17
      a1.isChangedBy(Changed("", "", Set(1, 2, 3, 4)), Seq(methodCodeBlock)) ==> false

      // test edge case, should be inclusive
      val changedResult =
        13.until(20)
          .map({ changedLine =>
            a1.isChangedBy(Changed("", "", Set(changedLine)), Seq(methodCodeBlock))
          })

      changedResult ==> Seq(false, false, true, true, true, false, false)
    }
  }
}