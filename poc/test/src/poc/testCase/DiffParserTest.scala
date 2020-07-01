package poc.testCase


import poc.domain._
import poc.javaParser.JavaGenerator
import utest._

import scala.util.Try


object DiffParserTest extends TestSuite {
  val detector = new DetectorByParser(new JavaGenerator(), null)

  val tests = Tests {

    "handleFallback should handle null" - {
      Try{
        Seq(null).collect(detector.handleFallback)
      }.failed.get.getMessage ==> "should handle all case."
    }

    "handleFallback should show missing type" - {
      Try{
        Seq(Created("","")).collect(detector.handleFallback)
      }.failed.get.getMessage ==> "should handle all case. missing class poc.domain.Created"




    }

  }
}