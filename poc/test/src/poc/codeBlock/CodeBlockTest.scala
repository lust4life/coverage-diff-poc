package poc.codeBlock

import utest._

import scala.util.Try

object CodeBlockTest extends TestSuite {

  val tests = Tests {
    val dummyRange = BlockRange(1, 1)

    "method's id should concat container's qualification" - {
      val m = Method(ClassOrInterface("", dummyRange, "a.b.c"), "Foo M1(Bar)", dummyRange)
      val id = m.id
      assert(id == "a.b.c\t#\tFoo M1(Bar)")
    }

    "method's file path should be container's" - {
      val m = Method(ClassOrInterface("a/b/c", dummyRange, ""), "", dummyRange)
      val filePath = m.filePath
      assert(filePath == "a/b/c")
    }

    "block range' begin should <= end" - {
      Try {
        BlockRange(8, 2)
      }.failed.get.getMessage ==> "requirement failed: begin should <= end"
    }
  }
}
