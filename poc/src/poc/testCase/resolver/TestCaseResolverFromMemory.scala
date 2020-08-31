package poc.testCase.resolver

import java.io.File

import poc.testCase.store.TestCaseMemoryStore
import poc.testCase.{TestCaseInfo, TestCaseResolver}

import scala.concurrent.Future

class TestCaseResolverFromMemory(packageRootPrefix: String, testCaseMemoryStore: TestCaseMemoryStore = new TestCaseMemoryStore())
  extends TestCaseResolver {

  val packageRootPrefixWithEnding =
    if (packageRootPrefix.endsWith(File.separator)) packageRootPrefix
    else packageRootPrefix + File.separator


  /**
   * retrieve a test case info by a file path
   *
   * @param filePath
   * @return
   */
  override def retrieve(filePath: String): Future[Seq[TestCaseInfo]] = {
    val infos =
      testCaseMemoryStore.store
        .filter(testCase => {
          testCase.coverage.exists(_.filePath.equalsIgnoreCase(filePath.stripPrefix(packageRootPrefixWithEnding)))
        })
        .map(testCaseInfo => {
          val affectedFilesWithPrefixPath =
            testCaseInfo.coverage.map(affectedFile => {
              val filePathWithPrefix = packageRootPrefixWithEnding + affectedFile.filePath
              affectedFile.copy(filePath = filePathWithPrefix)
            })
          testCaseInfo.copy(coverage = affectedFilesWithPrefixPath)
        })
        .toSeq
    Future.successful(infos)
  }
}