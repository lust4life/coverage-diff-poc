package poc.example

import java.io.FileOutputStream
import java.util.jar.{JarFile, JarOutputStream}
import java.util.zip.ZipEntry

import utest._

import scala.jdk.CollectionConverters.EnumerationHasAsScala

object utilsTest extends TestSuite {
  val tests = Tests {

    "chooses classes" - {
      val springBootFatJar = createSprintBootFatJar
      val classesInputStream = utils.chooseClassDirInSpringBootJar(springBootFatJar)
      val jarFile = new JarFile(os.temp(classesInputStream).toIO)
      val entries = jarFile.entries().asScala.length
      entries ==> 4
    }
  }

  val createSprintBootFatJar = {
    val tmpPath = os.temp()
    val jarStream = new JarOutputStream(new FileOutputStream(tmpPath.toIO))
    val mockClassesFiles = Seq(
      "META-INF/a",
      "org/springframework/boot/a",
      "BOOT-INF/classes/com/example/aService/AController.class",
      "BOOT-INF/classes/com/example/aService/AServiceApplication.class",
      "BOOT-INF/classes/com/example/aService/SharedLib$InnerSharedLib.class",
      "BOOT-INF/classes/com/example/aService/SharedLib.class")

    mockClassesFiles.foreach { entryName =>
      jarStream.putNextEntry(new ZipEntry(entryName))
      jarStream.closeEntry()
    }
    jarStream.close()

    tmpPath.toNIO
  }
}
