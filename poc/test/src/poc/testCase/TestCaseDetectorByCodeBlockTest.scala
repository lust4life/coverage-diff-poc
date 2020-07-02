package poc.testCase

import java.io.InputStream

import poc.codeBlock.{CodeBlockGenerator, CodeBlockGeneratorByFilePath, FilePathResolver}
import poc.codeBlock.javaGenerator.JavaBlockGenerator
import poc.diff._
import poc.testCase.detector.TestCaseDetectorByCodeBlock
import poc.testCase.resolver.TestCaseResolverFromDb
import utest._

import scala.util.Try
import scala.async.Async._
import scala.concurrent.ExecutionContext.Implicits.global


object TestCaseDetectorByCodeBlockTest extends TestSuite {
  val javaBlockGenerator = new JavaBlockGenerator() with CodeBlockGeneratorByFilePath with FilePathResolver {
    override def getStream(filePath: String): InputStream = ???
  }

  val testCaseResolver =  new TestCaseResolverFromDb() with FilterJavaDiff with TestCaseResolverByDiff

  val detector = new TestCaseDetectorByCodeBlock(javaBlockGenerator, testCaseResolver)

  val tests = Tests {

    "handleFallback should show missing type" - {
      Try {
        Seq((classOf[Created], Seq.empty)).collect(detector.handleFallback)
      }.failed.get.getMessage ==> "should handle all case. missing class poc.diff.Created"
    }

    "handle created file should return empty" - async {
      val res = await {
        detector.handleCreated(classOf[Created], Seq.empty)
      }

      res ==> Seq.empty
    }
  }
}