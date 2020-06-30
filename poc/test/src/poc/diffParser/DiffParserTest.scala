package poc.diffParser

import java.nio.file.{Files, Paths}

import utest._

object DiffParserTest extends TestSuite{
  val diffParser = new DiffParser

  val tests = Tests {

    "parse diff json file" - {
      val in = Files.newInputStream(Paths.get("poc/test/resources/diff.json"))
      val res = diffParser.parse(in)

      assert(res.isEmpty)
    }

  }
}
