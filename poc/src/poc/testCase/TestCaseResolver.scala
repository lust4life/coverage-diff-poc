package poc.testCase

import scala.concurrent.Future

trait TestCaseResolver {

  /**
   * retrieve a test case info by a file path
   *
   * @param filePath
   * @return
   */
  def retrieve(filePath: String): Future[Seq[TestCaseInfo]]
}
