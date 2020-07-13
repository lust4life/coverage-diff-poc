package poc.testCase.resolver

import poc.testCase.store.TestCaseMemoryStore
import poc.testCase.{TestCaseInfo, TestCaseResolver}

import scala.concurrent.Future

class TestCaseResolverFromMemory(testCaseMemoryStore: TestCaseMemoryStore = new TestCaseMemoryStore())
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
          testCase.coverage.exists(_.filePath.equalsIgnoreCase(filePath))
        })
        .toSeq
    Future.successful(infos)
  }
}