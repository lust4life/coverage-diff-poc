package poc.codeBlock
package filePathResolver

import java.io.{File, FileInputStream, InputStream}


class CodeBlockFilePathResolverByLocalFile(basedir: File) extends CodeBlockFilePathResolver {
  override def getStream(filePath: String): InputStream = {
    new FileInputStream(new File(basedir, filePath))
  }
}
