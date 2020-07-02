package poc.testCase

sealed trait TestCaseChangedReason

final case class FileDeleted(filePath: String) extends TestCaseChangedReason

final case class FileRenamed(from: String, to: String, changedMethods: Seq[String]) extends TestCaseChangedReason

final case class FileChanged(filePath: String, changedMethods: Seq[String]) extends TestCaseChangedReason

final case class UnknownError(filePath:String, reason: String) extends TestCaseChangedReason

final case class TestCaseChangedInfo(id: String, reasons: Seq[TestCaseChangedReason])
