package poc.testCase

import org.mockito.ArgumentMatchersSugar._
import org.mockito.MockitoSugar._
import poc.codeBlock.CodeBlockGenerator.Success
import poc.codeBlock.{BlockRange, ClassOrInterface, CodeBlockGeneratorByFilePath, Method}
import poc.diff._
import poc.testCase.detector.TestCaseDetectorByCodeBlock
import utest._

import scala.async.Async._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try


object TestCaseDetectorByCodeBlockTest extends TestSuite {

  val tests = Tests {
    "handleFallback should show missing type" - {
      val mockTestCaseResolver = mock[TestCaseResolver]
      when(mockTestCaseResolver.retrieve(anySeq)).thenCallRealMethod()

      val mockJavaBlockGenerator = mock[CodeBlockGeneratorByFilePath]
      val testCaseResolverByDiff = new TestCaseResolverByDiff(mockTestCaseResolver, JavaDiffFilter)
      val detector = new TestCaseDetectorByCodeBlock(mockJavaBlockGenerator, testCaseResolverByDiff)

      Try {
        Seq((classOf[Created], Seq.empty)).collect(detector.handleFallback)
      }.failed.get.getMessage ==> "should handle all case. missing class poc.diff.Created"
    }

    "handle created file should return empty" - async {
      val mockTestCaseResolver = mock[TestCaseResolver]
      when(mockTestCaseResolver.retrieve(anySeq)).thenCallRealMethod()

      val mockJavaBlockGenerator = mock[CodeBlockGeneratorByFilePath]
      val testCaseResolverByDiff = new TestCaseResolverByDiff(mockTestCaseResolver, JavaDiffFilter)
      val detector = new TestCaseDetectorByCodeBlock(mockJavaBlockGenerator, testCaseResolverByDiff)

      val res = await {
        detector.handleCreated(classOf[Created], Seq.empty)
      }

      res ==> Seq.empty
    }

    "handle deleted file should return empty" - async {
      val mockTestCaseResolver = mock[TestCaseResolver]
      when(mockTestCaseResolver.retrieve(anySeq)).thenCallRealMethod()

      val mockJavaBlockGenerator = mock[CodeBlockGeneratorByFilePath]
      val testCaseResolverByDiff = new TestCaseResolverByDiff(mockTestCaseResolver, JavaDiffFilter)
      val detector = new TestCaseDetectorByCodeBlock(mockJavaBlockGenerator, testCaseResolverByDiff)

      val filePath = "a.java"
      when(mockTestCaseResolver.retrieve(filePath))
        .thenReturn(Future.successful(Seq(TestCaseInfo("test case 1", Seq(AffectedFile(filePath, Seq.empty))))))

      when(mockTestCaseResolver.retrieve(anySeq)).thenCallRealMethod()

      val res = await {
        detector.handleDeleted(classOf[Deleted], Seq(Deleted(filePath, "java")))
      }

      res ==> Seq(TestCaseChangedInfo("test case 1", Seq(FileDeleted(filePath))))
    }

    "handle changed file should return empty" - async {
      val mockTestCaseResolver = mock[TestCaseResolver]
      when(mockTestCaseResolver.retrieve(anySeq)).thenCallRealMethod()

      val mockJavaBlockGenerator = mock[CodeBlockGeneratorByFilePath]
      val testCaseResolverByDiff = new TestCaseResolverByDiff(mockTestCaseResolver, JavaDiffFilter)
      val detector = new TestCaseDetectorByCodeBlock(mockJavaBlockGenerator, testCaseResolverByDiff)

      val filePath = "a.java"
      val mockMethod = "a.b.c\t#\tDoSomething(Int,String)"
      val affectedMethods = Seq(AffectedMethod(mockMethod), AffectedMethod("a.b.c\t#\tanother mockMethod"))
      when(mockTestCaseResolver.retrieve(filePath))
        .thenReturn(Future.successful(Seq(TestCaseInfo("test case 1", Seq(AffectedFile(filePath, affectedMethods))))))

      val classCodeBlock = ClassOrInterface(filePath, BlockRange(1, 40), "a.b.c")
      val method15to30 = Method(classCodeBlock, "DoSomething(Int,String)", BlockRange(15, 30))
      val method5to10 = Method(classCodeBlock, "another mockMethod", BlockRange(5, 10))
      when(mockJavaBlockGenerator.generate(filePath)).thenReturn(Right(Success(filePath, Seq(method5to10, method15to30))))

      val res = await {
        // only method15to30 changed
        val changedLines = Set(22.0)
        detector.handleChanged(classOf[Changed], Seq(Changed(filePath, "java", changedLines)))
      }

      res ==> Seq(TestCaseChangedInfo("test case 1", Seq(FileChanged(filePath, Seq(mockMethod)))))
    }

    "handle rename file should return empty" - async {
      val mockTestCaseResolver = mock[TestCaseResolver]
      when(mockTestCaseResolver.retrieve(anySeq)).thenCallRealMethod()

      val mockJavaBlockGenerator = mock[CodeBlockGeneratorByFilePath]
      val testCaseResolverByDiff = new TestCaseResolverByDiff(mockTestCaseResolver, JavaDiffFilter)
      val detector = new TestCaseDetectorByCodeBlock(mockJavaBlockGenerator, testCaseResolverByDiff)

      val fromFilePath = "a.java"
      val toFilePath = "b.java"
      val mockMethod = "a.b.c\t#\tDoSomething(Int,String)"
      val affectedMethods = Seq(AffectedMethod(mockMethod), AffectedMethod("a.b.c\t#\tanother mockMethod"))
      when(mockTestCaseResolver.retrieve(fromFilePath))
        .thenReturn(Future.successful(Seq(TestCaseInfo("test case 1", Seq(AffectedFile(fromFilePath, affectedMethods))))))

      val classCodeBlock = ClassOrInterface(fromFilePath, BlockRange(1, 40), "a.b.c")
      val method15to30 = Method(classCodeBlock, "DoSomething(Int,String)", BlockRange(15, 30))
      val method5to10 = Method(classCodeBlock, "another mockMethod", BlockRange(5, 10))
      when(mockJavaBlockGenerator.generate(toFilePath)).thenReturn(Right(Success(fromFilePath, Seq(method5to10, method15to30))))

      val res = await {
        // only method15to30 changed
        val changedLines = Set(22.0)
        detector.handleRename(classOf[Rename], Seq(Rename(fromFilePath, toFilePath, "java", changedLines)))
      }

      res ==> Seq(TestCaseChangedInfo("test case 1", Seq(FileRenamed(fromFilePath, toFilePath, Seq(mockMethod)))))
    }
  }
}
