package poc.testCase
package detector

import poc.codeBlock.CodeBlockGeneratorByFilePath
import poc.diff._
import poc.Implicits._

import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TestCaseDetectorByCodeBlock(codeBlockGenerator: CodeBlockGeneratorByFilePath, testCaseResolver: TestCaseResolverByDiff) extends TestCaseDetector {

  import TestCaseDetectorByCodeBlock._

  type DetectResult = Future[Seq[TestCaseChangedInfo]]

  val generateCodeBlockByCache = memorize(codeBlockGenerator.generate)

  /**
   * skip created file change
   */
  val handleCreated: PartialFunction[(Class[_], Seq[DiffFile]), DetectResult] = {
    case (x, _) if x == classOf[Created] => createdHandleResult
  }

  /**
   * find all affected test cases by deleted file,
   * should report all test cases include this file.
   */
  val handleDeleted: PartialFunction[(Class[_], Seq[DiffFile]), DetectResult] = {
    case (x, diffs) if x == classOf[Deleted] => async {
      await {
        testCaseResolver.retrieve(diffs)
      }.merged
        .map(testCase => {
          val reasonByDelete = testCase.coverage.map(affectedFile => FileDeleted(affectedFile.filePath))
          TestCaseChangedInfo(testCase.id, reasonByDelete)
        })
        .toSeq
    }
  }

  /**
   * using new file name to find file needed to parse, and using old file name to find test cases.
   */
  val handleRename: PartialFunction[(Class[_], Seq[DiffFile]), DetectResult] = {
    case (x, diffs) if x == classOf[Rename] => async {
      val renamedFiles = diffs.cast[Rename]
      await {
        testCaseResolver.retrieve(diffs)
      }.merged
        .map(testCase => {
          val reasonByRename =
            testCase.coverage.flatMap(affectedFile => {
              val fromFilePath = affectedFile.filePath
              renamedFiles
                .find(_.from.equalsIgnoreCase(fromFilePath))
                .map(renamedFile => {
                  val toFilePath = renamedFile.to

                  generateCodeBlockByCache(toFilePath)
                    .map(x => {
                      val changedMethods =
                        affectedFile.findChangedMethodBy(renamedFile, x.codeBlocks).map(_.signature)
                      FileRenamed(fromFilePath, toFilePath, changedMethods)
                    })
                    .fold(error => UnknownError(error.filePath, error.reason), identity)
                })
            })

          TestCaseChangedInfo(testCase.id, reasonByRename)
        })
        .toSeq
    }
  }

  val handleChanged: PartialFunction[(Class[_], Seq[DiffFile]), DetectResult] = {
    case (x, diffs) if x == classOf[Changed] => async {
      val changedFiles = diffs.cast[Changed]
      await {
        testCaseResolver.retrieve(diffs)
      }.merged
        .map(testCase => {
          val reasonByChange =
            testCase.coverage.flatMap(affectedFile => {
              val filePath = affectedFile.filePath

              changedFiles
                .find(_.filePath.equalsIgnoreCase(filePath))
                .map(changedFile => {
                  generateCodeBlockByCache(filePath)
                    .map(x => {
                      val changedMethods =
                        affectedFile.findChangedMethodBy(changedFile, x.codeBlocks).map(_.signature)
                      FileChanged(filePath, changedMethods)
                    })
                    .fold(error => UnknownError(error.filePath, error.reason), identity)
                })
            })

          TestCaseChangedInfo(testCase.id, reasonByChange)
        })
        .toSeq
    }
  }

  val handleFallback: PartialFunction[(Class[_], Seq[DiffFile]), DetectResult] = {
    case (x, _) => throw new NotImplementedError(s"should handle all case. missing $x")
  }

  /**
   * when we have diff files, we could find changed methods of test case coverage by these diff changed lines
   *
   * @param diffFiles diff files
   * @return
   */
  override def detect(diffFiles: Seq[DiffFile]): DetectResult = async {
    await {
      val allResults =
        diffFiles.groupBy(_.getClass)
          .collect(
            handleCreated orElse
              handleDeleted orElse
              handleChanged orElse
              handleRename orElse
              handleFallback)
          .toSeq

      Future.sequence(allResults)
    }.flatten
  }
}

object TestCaseDetectorByCodeBlock {
  private val createdHandleResult = Future.successful(Seq.empty)
}
