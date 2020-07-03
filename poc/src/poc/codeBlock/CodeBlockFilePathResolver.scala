package poc.codeBlock

import java.io.InputStream

trait CodeBlockFilePathResolver {
  def getStream(filePath: String): InputStream
}
