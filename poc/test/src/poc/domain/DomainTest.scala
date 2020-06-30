package poc.domain

import utest._

object DiffParserTest extends TestSuite{

  val tests = Tests {
    val dummyRange =BlockRange(1,1)

    "method's id should concat container's qualification" - {
      val m = Method(ClassOrInterface("",dummyRange,"a.b.c"),"Foo M1(Bar)",dummyRange)
      val id = m.id
      assert(id == "a.b.c\t#\tFoo M1(Bar)")
    }

    "method's file path should be container's" - {
      val m = Method(ClassOrInterface("a/b/c",dummyRange,""),"",dummyRange)
      val filePath = m.filePath
      assert(filePath == "a/b/c")
    }

    "block range' begin should <= end" - {
      try{
        BlockRange(8,2)
        assert(false)
      }catch{
        case ex => {
          val msg = ex.getMessage
          msg ==> "requirement failed: begin should <= end"
        }
      }
    }
  }
}
