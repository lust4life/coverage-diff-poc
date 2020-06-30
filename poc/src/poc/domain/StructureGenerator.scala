package poc.domain

import java.io.InputStream

trait StructureGenerator {
  import ParseResult._

  /**
   * 解析 source code 生成 CodeBlocks
   *
   * @param in 可能来源于 file 或者 vcs 的 api
   * @param filePath 用于调试，提供反馈用
   * @return
   */
  def parse(in: InputStream, filePath: String): Either[Error, Success]
}

object ParseResult {

  final case class Success(filePath: String, result: Seq[CodeBlock])

  final case class Error(filePath: String, reason: String)

}
