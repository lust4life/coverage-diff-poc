package poc.testCase

import scala.concurrent.Future

trait TestCaseStore {

  def save(coverageInfo: TestCaseInfo): Future[Unit]

}
