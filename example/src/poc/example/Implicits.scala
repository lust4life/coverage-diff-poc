package poc.example

import poc.testCase._

trait Implicits {
  implicit def fileDeletedRW = upickle.default.macroRW[FileDeleted]

  implicit def fileRenamedRW = upickle.default.macroRW[FileRenamed]

  implicit def fileChangedRW = upickle.default.macroRW[FileChanged]

  implicit def unknownErrorRW = upickle.default.macroRW[UnknownError]

  implicit def testCaseChangedReasonRW = upickle.default.macroRW[TestCaseChangedReason]

  implicit def testCaseChangedInfoRW = upickle.default.macroRW[TestCaseChangedInfo]

  implicit def affectedMethodRW = upickle.default.macroRW[AffectedMethod]

  implicit def affectedFileRW = upickle.default.macroRW[AffectedFile]

  implicit def testCaseInfoInfoRW = upickle.default.macroRW[TestCaseInfo]
}

object Implicits extends Implicits