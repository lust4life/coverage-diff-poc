package poc.domain

final case class BlockRange(begin: Int, end: Int)

trait CodeBlock {
  def Id: String
  def Range: BlockRange
}


