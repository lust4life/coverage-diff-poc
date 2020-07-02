package poc.codeBlock

import java.io.InputStream

trait FilePathResolver {
  def getStream(filePath: String): InputStream
}

