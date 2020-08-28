package poc.example

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, FileInputStream, InputStream, OutputStream, PipedInputStream, PipedOutputStream}
import java.net.Socket
import java.nio.file.{Files, Path}
import java.util.zip.{ZipFile, ZipOutputStream}

import poc.Implicits._
import poc.jacoco.JacocoClient

import scala.jdk.CollectionConverters.EnumerationHasAsScala
import scala.util.Using

object utils {

  val port = 20201

  val jacocoClient = new JacocoClient(
    new Socket("localhost", port)
  )

  val mockServiceUrl = fromResource("mockservice.jar")

  def startJacocoAgent() = {
    val mockServiceFilePath = mockServiceUrl.getFile
    val jacocoagentJar = fromResource("jacocoagent.jar").getFile
    val sub =
      os.proc(
        "java",
        s"-javaagent:$jacocoagentJar=address=*,port=$port,output=tcpserver",
        "-jar", mockServiceFilePath).spawn()
    sub
  }

  def chooseClassDirInSpringBootJar(jarPath: Path): InputStream = {
    val out = new ByteArrayOutputStream()
    val tmpZipStream = new ZipOutputStream(out)
    val classFilePattern = """BOOT-INF/classes/.*\.class""".r
    Using(new ZipFile(jarPath.toFile)) { zipFile =>
      zipFile.entries().asScala.foreach { zipEntry =>
        if (zipEntry.getName.matches("""BOOT-INF/classes/.*\.class""")) {
          tmpZipStream.putNextEntry(zipEntry)
          zipFile.getInputStream(zipEntry).transferTo(tmpZipStream)
          tmpZipStream.closeEntry()
        }
      }
      tmpZipStream.close()
    }

    new ByteArrayInputStream(out.toByteArray)
  }
}
