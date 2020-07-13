package poc.testCase

import scala.concurrent.Future
import scala.util.Try

trait TestCaseStore {

  def save(coverageInfo: TestCaseInfo): Future[Try[Unit]]

}
