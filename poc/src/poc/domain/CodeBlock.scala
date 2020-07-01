package poc.domain

import java.io.InputStream

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


trait StructureGenerator {

  import StructureGenerator._

  /**
   * 解析 source code 生成 CodeBlocks
   *
   * @param in       可能来源于 file 或者 vcs 的 api
   * @param filePath 用于调试，提供反馈用
   * @return
   */
  def parse(in: InputStream, filePath: String): Either[Error, Success]
}

object StructureGenerator {

  final case class Success(filePath: String, result: Seq[CodeBlock])

  final case class Error(filePath: String, reason: String)

}