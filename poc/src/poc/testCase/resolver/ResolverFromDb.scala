package poc.testCase.resolver

import poc.testCase.{TestCaseInfo, TestCaseResolver}

import scala.concurrent.Future

class ResolverFromDb extends TestCaseResolver{
  /**
   * retrieve a test case info by a file path
   *
   * @param filePath
   * @return
   */
  override def retrieve(filePath: String): Future[Seq[TestCaseInfo]] = ???
}
