package poc.testCase.detector

import org.mockito.ArgumentMatchersSugar._
import org.mockito.MockitoSugar._
import poc.Implicits._
import poc.codeBlock.javaGenerator.JavaBlockGenerator
import poc.codeBlock.{CodeBlockFilePathResolver, CodeBlockGeneratorByFilePath}
import poc.diff.JavaDiffFilter
import poc.diff.jsonGenerator.DiffJsonParser
import poc.testCase.{AffectedFile, AffectedMethod, TestCaseInfo, TestCaseResolver, TestCaseResolverByDiff}
import utest._

import scala.async.Async._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TestCaseDetectorByDiffStreamTest extends TestSuite {

  val tests: Tests = Tests {
    val mockTestCaseResolver = mock[TestCaseResolver]
    val testCaseResolverByDiff = new TestCaseResolverByDiff(mockTestCaseResolver, JavaDiffFilter)
    val mockCodeBlockFilePathResolver = mock[CodeBlockFilePathResolver]
    val codeBlockGenerator = new CodeBlockGeneratorByFilePath(new JavaBlockGenerator, mockCodeBlockFilePathResolver)
    val testCaseDetector = new TestCaseDetectorByCodeBlock(codeBlockGenerator, testCaseResolverByDiff)
    val detectorByDiffStream = new TestCaseDetectorByDiffStream(new DiffJsonParser, testCaseDetector)

    when(mockTestCaseResolver.retrieve(anySeq)).thenCallRealMethod()
    val sharedLibPath = "src/main/java/com/poc/SharedLib.java"
    val testCaseEntryPath = "src/main/java/com/poc/TestCaseEntry.java"
    when(mockTestCaseResolver.retrieve(any[String])).thenAnswer({
      case `sharedLibPath` => {
        Future.successful(Seq(
          TestCaseInfo("TestCase1", Seq(
            AffectedFile(sharedLibPath, Seq(
              AffectedMethod("com.poc.SharedLib\t#\tDoubleNumber(Integer)"),
              AffectedMethod("com.poc.SharedLib\t#\tCommonLogInfo(T)")
            ))
          )),
          TestCaseInfo("TestCase2", Seq(
            AffectedFile(sharedLibPath, Seq(
              AffectedMethod("com.poc.SharedLib\t#\tUseInnerClass(T)"),
              AffectedMethod("com.poc.SharedLib.InnerSharedLib\t#\tIdentity(T)")
            ))
          )),
          TestCaseInfo("TestCase3", Seq(
            AffectedFile(sharedLibPath, Seq(
              AffectedMethod("com.poc.SharedLib\t#\tCommonLogInfo(T)"),
            ))
          ))
        ))
      }
      case `testCaseEntryPath` => {
        Future.successful(Seq(
          TestCaseInfo("TestCase1", Seq(
            AffectedFile(testCaseEntryPath, Seq(
              AffectedMethod("com.poc.TestCaseEntry\t#\tTestCase1()"),
              AffectedMethod("com.poc.TestCaseEntry\t#\tDoubleNumIfMoreThan5(Integer)"),
            ))
          )),
          TestCaseInfo("TestCase2", Seq(
            AffectedFile(testCaseEntryPath, Seq(
              AffectedMethod("com.poc.TestCaseEntry\t#\tTestCase2()"),
              AffectedMethod("com.poc.TestCaseEntry\t#\tDoubleNumIfMoreThan5(Integer)"),
            ))
          )),
          TestCaseInfo("TestCase3", Seq(
            AffectedFile(testCaseEntryPath, Seq(
              AffectedMethod("com.poc.TestCaseEntry\t#\tTestCase3()"),
            ))
          )),
        ))
      }
    }: PartialFunction[Any, Future[Seq[TestCaseInfo]]])

    when(mockCodeBlockFilePathResolver.getStream(any[String])).thenAnswer((filePath: String) => {
      getClass.getClassLoader.getResourceAsStream(filePath)
    })

    "change testcase 1" - async {
      val in = fromResource("testcase1.json").openStream()
      val testCaseChangedInfos = await {
        detectorByDiffStream.detect(in)
      }
      in.close()

      println(testCaseChangedInfos)
      testCaseChangedInfos.length ==> 1
    }

    "change testcase 2" - async {
      val in = fromResource("testcase2.json").openStream()
      val testCaseChangedInfos = await {
        detectorByDiffStream.detect(in)
      }
      in.close()

      println(testCaseChangedInfos)
      testCaseChangedInfos.length ==> 1
    }

    "change testcase 1 and 2" - async {
      val in = fromResource("testcase1.2.json").openStream()
      val testCaseChangedInfos = await {
        detectorByDiffStream.detect(in)
      }
      in.close()

      println(testCaseChangedInfos)
      testCaseChangedInfos.length ==> 2
    }

    "change testcase 1 and 2 and 3" - async {
      val in = fromResource("testcase.1.2.3.json").openStream()
      val testCaseChangedInfos = await {
        detectorByDiffStream.detect(in)
      }
      in.close()

      println(testCaseChangedInfos)
      testCaseChangedInfos.length ==> 3
    }
  }

}
