package poc.testCase

import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait TestCaseResolver {

  /**
   * retrieve a test case info by a file path
   *
   * @param filePath
   * @return
   */
  def retrieve(filePath: String): Future[Seq[TestCaseInfo]]

  /**
   * default impl using seq.map retrieve
   *
   * @param filePaths
   * @return
   */
  def retrieve(filePaths: Seq[String]): Future[Seq[TestCaseInfo]] = async {
    await {
      val allRes = filePaths.map(retrieve(_))
      Future.sequence(allRes)
    }.flatten
  }
}



