package poc.diff.jsonGenerator

import poc.Implicits.fromResource
import poc.diff.{Changed, Rename}
import utest._

object DiffGeneratorByUnifiedDiffTest extends TestSuite {
  val tests = Tests {

    val generator = new DiffGeneratorByUnifiedDiff()

    "invoke diff2html cmd to parse diff file into json" - {
      "parse succeed case" - {
        val in = fromResource("test.diff").openStream()

        val diffFiles = generator.generate(in)

        diffFiles.length ==> 4

        diffFiles.collect({
          case _: Changed =>
        }).length ==> 3

        diffFiles.collect({
          case _: Rename =>
        }).length ==> 1
      }

      "parse failed case" - {
        // diff.json is not a unified diff file
        val in = fromResource("diff.json").openStream()
        val diffFiles = generator.generate(in)
        diffFiles.length ==> 0
      }
    }
  }
}
