package poc.domain

import scala.concurrent.Future

/**
 * test case coverage info from db or elsewhere,
 * in order to detect changed info
 *
 * @param id       test case identity
 * @param coverage {filePath : ["method1","method2"...]}
 */
final case class TestCaseInfo(id: String, coverage: Map[String, Set[String]])

sealed trait TestCaseChangedReason

final case class FileDeleted(filePath: String) extends TestCaseChangedReason

final case class FileRenamed(from: String, to: String, changedMethods: Seq[String]) extends TestCaseChangedReason

final case class FileChanged(filePath: String, changedMethods: Seq[String]) extends TestCaseChangedReason

final case class TestCaseChangedInfo(id: String, reasons: Seq[TestCaseChangedReason])

trait TestCaseResolver {

  /**
   * retrieve a test case info by a file path
   *
   * @param filePath
   * @return
   */
  def retrieve(filePath: String): TestCaseInfo
}

trait TestCaseDetector {

  /**
   * when we have diff files, we could find changed methods of test case coverage by these diff changed lines
   *
   * @param diffFiles
   * @return
   */
  def detect(diffFiles: Seq[DiffFile]): Future[Seq[TestCaseChangedInfo]]
}