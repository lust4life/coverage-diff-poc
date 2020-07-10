package poc.codeBlock

import poc.diff.DiffFile

final case class BlockRange(begin: Int, end: Int) {
  require(begin <= end, "begin should <= end")
}

sealed trait CodeBlock {
  def id: String

  def range: BlockRange

  def filePath: String

  def isChangedByDiff(diffFile: DiffFile): Boolean = {
    diffFile.hasChangedLineIn(range)
  }
}

final case class ClassOrInterface(filePath: String, range: BlockRange, fullQualification: String) extends CodeBlock {
  override val id: String = fullQualification
}

final case class Method(container: ClassOrInterface, signature: String, range: BlockRange) extends CodeBlock {
  override def id: String = CodeBlock.generateSignatureForMethod(container.id, signature)

  override def filePath: String = container.filePath
}

object CodeBlock {
  def generateSignatureForMethod(classSignature: String, methodSignature: String) = classSignature + "\t#\t" + methodSignature
}