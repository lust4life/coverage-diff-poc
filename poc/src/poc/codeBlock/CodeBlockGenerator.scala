package poc.codeBlock

import java.io.InputStream

trait CodeBlockGenerator {

  import CodeBlockGenerator._

  /**
   * 解析 source code 生成 CodeBlocks
   *
   * @param in       可能来源于 file 或者 vcs 的 api
   * @param filePath 用于调试，提供反馈用
   * @return
   */
  def generate(in: InputStream, filePath: String): GenerateResult
}

object CodeBlockGenerator {

  final case class Success(filePath: String, codeBlocks: Seq[CodeBlock])

  final case class Error(filePath: String, reason: String)

  type GenerateResult = Either[Error, Success]
}



