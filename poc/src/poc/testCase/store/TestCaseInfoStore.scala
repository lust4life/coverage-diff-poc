package poc.testCase
package store

import scala.concurrent.Future
import scala.util.Try

trait TestCaseInfoStore {

  def save(coverageInfo: TestCaseInfo): Future[Try[Unit]]

}
