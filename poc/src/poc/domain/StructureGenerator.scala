package poc.domain

import java.io.InputStream

trait StructureGenerator {
  import ParseResult._

  def parse(in: InputStream, filePath: String): Either[Error, Success]
}

object ParseResult {

  final case class Success(filePath: String, result: Seq[CodeBlock])

  final case class Error(filePath: String, reason: String)

}
