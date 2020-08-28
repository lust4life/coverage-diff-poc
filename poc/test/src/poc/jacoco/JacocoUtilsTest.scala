package poc.jacoco

import poc.Implicits._
import poc.test.utils
import utest._

import scala.jdk.CollectionConverters._

object JacocoUtilsTest extends TestSuite {

  val tests = Tests {

    "analyze coverage from exec data and jar files" - {
      val (_, execDataStore) = utils.getMockCoverageExecData()

      val jarFileUrl = fromResource("mockservice.jar")
      val jarFilePath = jarFileUrl.getPath
      val jarFileStream = jarFileUrl.openStream()
      val bundleName = "test case 1 bundle"
      val bundle = JacocoUtils.analyzeCoverage(bundleName, jarFilePath, jarFileStream, execDataStore)

      bundle.getName ==> bundleName
      bundle.getPackages.size() ==> 2
      bundle.getClassCounter.getTotalCount ==> 4
      bundle.getClassCounter.getCoveredCount ==> 2

      val sourceFileNames =
        bundle.getPackages.asScala
          .flatMap(_.getSourceFiles.asScala.map(_.getName))
          .toSeq
          .sorted

      sourceFileNames ==> Seq("SharedLib.java", "Test.java", "TestCaseEntry.java")
    }
  }
}
