package poc.testCase

import poc.codeBlock.CodeBlock
import poc.diff.DiffFile

/**
 * test case coverage info from db or elsewhere,
 * in order to detect changed info
 *
 * @param id       test case identity
 * @param coverage affected files
 */
final case class TestCaseInfo(id: String, coverage: Seq[AffectedFile]) {
  def +(other: TestCaseInfo) = {
//    this.copy(coverage = coverage.reduc)

    ???
  }
}

final case class AffectedFile(filePath: String, affectedMethods: Seq[AffectedMethod]) {
  def findChangedMethodBy(diffFile: DiffFile, codeBlocks: Seq[CodeBlock]) = {
    affectedMethods.filter(_.isChangedBy(diffFile, codeBlocks))
  }
}

final case class AffectedMethod(signature: String) {

  /**
   * method not in code blocks means removed in new file, should consider be changed
   *
   * @param diffFile
   * @param codeBlocks
   * @return
   */
  def isChangedBy(diffFile: DiffFile, codeBlocks: Seq[CodeBlock]) = {
    codeBlocks.find(_.id == signature).forall(_.isChangedByDiff(diffFile))
  }
}
