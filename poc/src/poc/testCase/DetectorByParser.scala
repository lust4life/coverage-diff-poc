package poc.testCase

import poc.domain.{Changed, Created, Deleted, DiffFile, Rename, StructureGenerator, TestCaseChangedInfo, TestCaseDetector, TestCaseResolver}

import scala.concurrent.Future
import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global


class DetectorByParser(codeBlockGenerator: StructureGenerator, testCaseResolver: TestCaseResolver) extends TestCaseDetector {

  import DetectorByParser._

  type DetectResult = Future[Seq[TestCaseChangedInfo]]

  /**
   * skip created file change
   */
  val handleCreated: PartialFunction[DiffFile, DetectResult] = {
    case Created(_, _) => createdHandleResult
  }

  /**
   * find all affected test cases by deleted file
   */
  val handleDeleted: PartialFunction[DiffFile, DetectResult] = {
    case Deleted(filePath, language) => {
      val fileInputStream = ???
      val parseResult = codeBlockGenerator.parse(fileInputStream, "filePath")
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

object DetectorByParser {
  private val createdHandleResult = Future.successful(Seq.empty)
}

