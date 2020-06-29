package poc.domain

final case class BlockRange(begin: Int, end: Int)

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
    container.id + "\b$\b" + signature
  }

  override def filePath = container.filePath
}
