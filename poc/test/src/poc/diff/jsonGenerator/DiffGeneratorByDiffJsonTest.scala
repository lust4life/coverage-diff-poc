package poc.diff.jsonGenerator

import poc.diff.{Changed, Rename}
import utest._
import poc.Implicits._

import scala.collection.Set

object DiffGeneratorByDiffJsonTest extends TestSuite {
  val diffParser = new DiffGeneratorByDiffJson

  val tests = Tests {

    "parse diff json file" - {

      val in = fromResource("diff.json").openStream()
      val res = diffParser.generate(in)

      val resLen = res.length
      resLen ==> 4

      val d1 = res(0)
      assertMatch(d1) {
        case Changed("src/main/java/com/poc/Bar.java", "java", _) =>
      }

      val d2 = res(1)
      assertMatch(d2) {
        case Changed("src/main/java/com/poc/Foo.java", "java", _) =>
      }

      val d3 = res(2)
      assertMatch(d3) {
        case Changed("src/test/java/FooTest.java", "java", _) =>
      }

      val changedLines = Set(23.0, 28.0)
      val d4 = res(3)
      assertMatch(d4) {
        case Rename("src/main/java/com/poc/Baz.java", "src/main/java/com/poc/Baz1.java", "java", `changedLines`) =>
      }
    }
  }
}
