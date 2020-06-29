package poc.domain

import java.io.InputStream

import scala.util.Try

trait StructureGenerator {
  def parse(in: InputStream, filePath: String): Try[Seq[CodeBlock]]
}