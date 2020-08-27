package poc.codeBlock

import poc.codeBlock.filePathResolver.CodeBlockFilePathResolverByLocalFile
import utest._

import scala.io.Source

object CodeBlockFilePathResolverByLocalFileTest extends TestSuite {

  val tempDir = os.temp.dir()
  val filePathResolver = new CodeBlockFilePathResolverByLocalFile(tempDir.toIO)

  val tests = Tests {

    "should read from local file" - {
      val filePath = "hello"
      os.write(tempDir / filePath, "world")
      val in = filePathResolver.getStream(filePath)
      Source.fromInputStream(in).mkString ==> "world"
    }
  }
}
