package poc.example

import java.nio.file.Path
import java.util.zip.ZipFile

import poc.Implicits._
import utest._

import scala.jdk.CollectionConverters.EnumerationHasAsScala

object utilsTest extends TestSuite {
  val tests = Tests {

    "chooses classes" - {
      val springBootFatJar = fromResource("aService-0.0.1-SNAPSHOT.jar")
      val classesInputStream = utils.chooseClassDirInSpringBootJar(Path.of(springBootFatJar.getFile))
      val zipFile = new ZipFile(os.temp(classesInputStream).toIO)
      val entries = zipFile.entries().asScala.length
      entries ==> 4
    }
  }
}
