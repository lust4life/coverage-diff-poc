package poc.codeBlock

import poc.codeBlock.filePathResolver.CodeBlockFilePathResolverByLocalFile
import utest._

import scala.io.Source

object CodeBlockFilePathResolverByLocalFileTest extends TestSuite {

  val filePathResolver = new CodeBlockFilePathResolverByLocalFile

  val tests = Tests {

    "should read from local file" - {
      val fileUrl = getClass.getClassLoader.getResource("Invalid.java")

      val in = filePathResolver.getStream(fileUrl.getPath)
      Source.fromInputStream(in).mkString ==> "some data"
    }
  }
}
