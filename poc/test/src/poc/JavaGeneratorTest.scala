package poc

import java.io.{File, FileInputStream}

import poc.javaParser.JavaGenerator
import utest._

object JavaGeneratorTest extends TestSuite{
  val generator = new JavaGenerator()

  val tests = Tests{
    'testParse - {
      val res = generator.parse(new FileInputStream(new File("/Users/jiajun.qian/git/jacoco/spike/src/main/java/com/poc/Bar.java")),
        "some file path")

      assert(res.isSuccess)
    }
  }
}