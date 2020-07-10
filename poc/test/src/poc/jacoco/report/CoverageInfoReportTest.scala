package poc.jacoco.report

import org.jacoco.core.data.{ExecutionDataReader, ExecutionDataStore, SessionInfo, SessionInfoStore}
import utest._
import poc.Implicits._
import scala.jdk.CollectionConverters._

object CoverageInfoReportTest extends TestSuite {

  val (sessionStore, execDataStore) = getCoverageExecData()
  val report = new CoverageInfoReport()

  val tests = Tests {

    "generate coverage info report" - {
      val jarFileUrl = fromResource("mockservice.jar")
      val jarFilePath = jarFileUrl.getPath
      val jarFileStream = jarFileUrl.openStream()
      val bundleName = "test case 1 bundle"
      val bundle = report.analyzeCoverage(bundleName, jarFilePath, jarFileStream, execDataStore)

      bundle.getPackages.size() ==> 1
      bundle.getName ==> bundleName
      bundle.getClassCounter.getTotalCount ==> 3
      bundle.getClassCounter.getCoveredCount ==> 2

      val packageInfo = bundle.getPackages.asScala.head
      packageInfo.getName ==> "poc/example/javacode"
      val sourceFileNames = packageInfo.getSourceFiles.asScala.map(_.getName).toSeq.sorted
      sourceFileNames ==> Seq("SharedLib.java", "TestCaseEntry.java")
    }
  }

  def getCoverageExecData() = {
    val in = fromResource("coverage.exec").openStream()
    val reader = new ExecutionDataReader(in)

    val sessionStore = new SessionInfoStore()
    val execDataStore = new ExecutionDataStore()
    reader.setExecutionDataVisitor(execDataStore)
    reader.setSessionInfoVisitor(sessionStore)

    reader.read()
    in.close()

    (sessionStore, execDataStore)
  }
}
