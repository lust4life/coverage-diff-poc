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
final case class TestCaseInfo(id: String, sourceCodeVersion: String, coverage: Seq[AffectedFile]) {
  def +(other: TestCaseInfo): TestCaseInfo = {
    if (id.equalsIgnoreCase(other.id)) {
      val allCoverage = coverage ++ other.coverage
      val merged = allCoverage.groupBy(_.filePath).view.mapValues(_.reduce(_ + _)).values.toSeq
      this.copy(coverage = merged)
    } else {
      this
    }
  }
}

final case class AffectedFile(filePath: String, affectedMethods: Seq[AffectedMethod]) {
  def findChangedMethodBy(diffFile: DiffFile, codeBlocks: Seq[CodeBlock]): Seq[AffectedMethod] = {
    affectedMethods.filter(_.isChangedBy(diffFile, codeBlocks))
  }

  def +(other: AffectedFile): AffectedFile = {
    if (filePath.equalsIgnoreCase(other.filePath)) {
      val allMethods = affectedMethods ++ other.affectedMethods
      this.copy(affectedMethods = allMethods.distinct)
    }
    else {
      this
    }
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
  def isChangedBy(diffFile: DiffFile, codeBlocks: Seq[CodeBlock]): Boolean = {
    codeBlocks.find(_.id == signature).forall(_.isChangedByDiff(diffFile))
  }
}

object AffectedMethod {
  def apply(classSignature: String, methodSignature: String): AffectedMethod = {
    this (CodeBlock.generateSignatureForMethod(classSignature, methodSignature))
  }
}