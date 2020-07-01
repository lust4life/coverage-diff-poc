package poc.codeBlock

final case class BlockRange(begin: Int, end: Int) {
  require(begin <= end, "begin should <= end")
}

sealed trait CodeBlock {
  def id: String

  def range: BlockRange

  def filePath: String
}

final case class ClassOrInterface(filePath: String, range: BlockRange, fullQualification: String) extends CodeBlock {
  override val id = fullQualification

}

final case class Method(container: ClassOrInterface, signature: String, range: BlockRange) extends CodeBlock {
  override def id = {
    container.id + "\t#\t" + signature
  }

  override def filePath = container.filePath
}
