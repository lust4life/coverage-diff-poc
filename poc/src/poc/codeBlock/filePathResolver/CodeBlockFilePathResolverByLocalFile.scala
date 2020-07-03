package poc.codeBlock
package filePathResolver

import java.io.InputStream
import java.nio.file.{Files, Paths}


class CodeBlockFilePathResolverByLocalFile extends CodeBlockFilePathResolver {
  override def getStream(filePath: String): InputStream = {
    Files.newInputStream(Paths.get(filePath))
  }
}
