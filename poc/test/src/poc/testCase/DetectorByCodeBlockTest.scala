package poc.testCase

import poc.codeBlock.javaGenerator.JavaBlockGenerator
import poc.diff._
import poc.testCase.detector.DetectorByCodeBlock
import poc.testCase.resolver.ResolverFromDb
import utest._

import scala.util.Try
import scala.async.Async._
import scala.concurrent.ExecutionContext.Implicits.global

object DetectorByCodeBlockTest extends TestSuite {
  val detector = new DetectorByCodeBlock(new JavaBlockGenerator(), new ResolverFromDb())

  val tests = Tests {

    "handleFallback should handle null" - {
      Try {
        Seq(null).collect(detector.handleFallback)
      }.failed.get.getMessage ==> "should handle all case."
    }

    "handleFallback should show missing type" - {
      Try {
        Seq(Created("", "")).collect(detector.handleFallback)
      }.failed.get.getMessage ==> "should handle all case. missing class poc.diff.Created"
    }

    "handle created file should return empty" - async {
      val res = await {
        detector.handleCreated(Created("", ""))
      }

      res ==> Seq.empty
    }
  }
}