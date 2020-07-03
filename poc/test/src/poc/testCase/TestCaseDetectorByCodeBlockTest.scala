package poc.testCase

import poc.codeBlock.CodeBlockGeneratorByFilePath
import poc.codeBlock.filePathResolver.CodeBlockFilePathResolverByLocalFile
import poc.codeBlock.javaGenerator.JavaBlockGenerator
import poc.diff._
import poc.testCase.detector.TestCaseDetectorByCodeBlock
import poc.testCase.resolver.TestCaseResolverFromDb
import utest._

import scala.async.Async._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try


object TestCaseDetectorByCodeBlockTest extends TestSuite {
  val javaBlockGenerator = new CodeBlockGeneratorByFilePath(new JavaBlockGenerator, new CodeBlockFilePathResolverByLocalFile)
  val testCaseResolver = new TestCaseResolverByDiff(new TestCaseResolverFromDb, JavaDiffFilter)
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