package poc.testCase
package detector

import poc.codeBlock.CodeBlockGenerator
import poc.diff.{Changed, Created, Deleted, DiffFile, Rename}

import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class DetectorByCodeBlock(codeBlockGenerator: CodeBlockGenerator, testCaseResolver: TestCaseResolver) extends TestCaseDetector {

  import DetectorByCodeBlock._

  type DetectResult = Future[Seq[TestCaseChangedInfo]]

  /**
   * skip created file change
   */
  val handleCreated: PartialFunction[DiffFile, DetectResult] = {
    case Created(_, _) => createdHandleResult
  }

  /**
   * find all affected test cases by deleted file,
   * should report all test cases include this file.
   */
  val handleDeleted: PartialFunction[DiffFile, DetectResult] = {
    case Deleted(filePath, language) => async {
      await {
        testCaseResolver.retrieve(filePath)
      }.map(testCaseInfo =>{

      })

      //      testCaseResolver.re
      val fileInputStream = ???
      val parseResult = codeBlockGenerator.generate(fileInputStream, "filePath")
      parseResult.left
      ???
    }
  }

  val handleRename: PartialFunction[DiffFile, DetectResult] = {
    case Rename(_, _, _, _) => ???
  }

  val handleChanged: PartialFunction[DiffFile, DetectResult] = {
    case Changed(_, _, _) => ???
  }

  val handleFallback: PartialFunction[DiffFile, DetectResult] = {
    case x => throw new NotImplementedError(s"should handle all case.${if (x == null) "" else " missing " + x.getClass}")
  }

  /**
   * when we have diff files, we could find changed methods of test case coverage by these diff changed lines
   *
   * @param diffFiles
   * @return
   */
  override def detect(diffFiles: Seq[DiffFile]): DetectResult = async {
    await {
      val allResults =
        diffFiles.collect(
          handleCreated orElse
            handleDeleted orElse
            handleChanged orElse
            handleRename orElse
            handleFallback)

      Future.sequence(allResults)
    }.flatten
  }
}

object DetectorByCodeBlock {
  private val createdHandleResult = Future.successful(Seq.empty)
}

