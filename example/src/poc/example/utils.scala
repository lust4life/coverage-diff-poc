package poc.example

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, InputStream}
import java.nio.file.Path
import java.util.jar.{JarFile, JarOutputStream}

import poc.Implicits._

import scala.jdk.CollectionConverters.EnumerationHasAsScala
import scala.util.Using

object utils {

  val mockServiceUrl = fromResource("mockservice.jar")

  def startJacocoAgent(serviceJarPath: Path, tcpServerPort: Int) = {
    val mockServiceFilePath = serviceJarPath.toString
    val jacocoagentJar = fromResource("jacocoagent.jar").getFile
    val sub =
      os.proc(
        "java",
        s"-javaagent:$jacocoagentJar=address=*,port=$tcpServerPort,output=tcpserver",
        "-jar", mockServiceFilePath).spawn()
    sub
  }

  def chooseClassDirInSpringBootJar(jarPath: Path): InputStream = {
    val out = new ByteArrayOutputStream()
    val tmpJarStream = new JarOutputStream(out)
    Using(new JarFile(jarPath.toFile)) { jarFile =>
      jarFile.entries().asScala.foreach { jarEntry =>
        if (jarEntry.getName.matches("""BOOT-INF/classes/.*\.class""")) {
          tmpJarStream.putNextEntry(jarEntry)
          jarFile.getInputStream(jarEntry).transferTo(tmpJarStream)
          tmpJarStream.closeEntry()
        }
      }
      tmpJarStream.close()
    }

    new ByteArrayInputStream(out.toByteArray)
  }
}
