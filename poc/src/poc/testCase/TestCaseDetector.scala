package poc.testCase

import poc.diff.DiffFile

import scala.concurrent.Future

trait TestCaseDetector {

  /**
   * when we have diff files, we could find changed methods of test case coverage by these diff changed lines
   *
   * @param diffFiles
   * @return
   */
  def detect(diffFiles: Seq[DiffFile]): Future[Seq[TestCaseChangedInfo]]
}


