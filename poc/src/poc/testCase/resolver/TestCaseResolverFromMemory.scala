package poc.testCase.resolver

import poc.testCase.store.TestCaseMemoryStore
import poc.testCase.{TestCaseInfo, TestCaseResolver}

import scala.concurrent.Future

class TestCaseResolverFromMemory(packageRootPrefix: String, testCaseMemoryStore: TestCaseMemoryStore = new TestCaseMemoryStore())
  extends TestCaseResolver {

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
          testCase.coverage.exists(_.filePath.equalsIgnoreCase(filePath.stripPrefix(packageRootPrefix)))
        })
        .map(testCaseInfo => {
          val affectedFilesWithPrefixPath =
            testCaseInfo.coverage.map(affectedFile => {
              val filePathWithPrefix = packageRootPrefix + affectedFile.filePath
              affectedFile.copy(filePath = filePathWithPrefix)
            })
          testCaseInfo.copy(coverage = affectedFilesWithPrefixPath)
        })
        .toSeq
    Future.successful(infos)
  }
}