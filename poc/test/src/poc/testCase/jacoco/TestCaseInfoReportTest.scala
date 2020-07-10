package poc.testCase.jacoco

import org.jacoco.core.data.{ExecutionDataReader, ExecutionDataStore, SessionInfoStore}
import poc.Implicits.fromResource
import poc.testCase.{AffectedFile, AffectedMethod, TestCaseInfo}
import utest._

import scala.jdk.CollectionConverters._

object TestCaseInfoFromJacocoTest extends TestSuite {

  val (sessionStore, execDataStore) = getCoverageExecData()
  val report = new TestCaseInfoFromJacoco()

  val tests = Tests {

    "analyze coverage from exec data and jar files" - {
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

    "generate test case info from jacoco coverage info" - {
      val jarFileUrl = fromResource("mockservice.jar")
      val jarFilePath = jarFileUrl.getPath
      val jarFileStream = jarFileUrl.openStream()
      val bundleName = "test case 1 bundle"
      val bundle = report.analyzeCoverage(bundleName, jarFilePath, jarFileStream, execDataStore)
      val sourceCodeVersion = "some code version"
      val testCaseInfo = report.generateTestCaseInfo(sourceCodeVersion, bundle)

      testCaseInfo ==>
        Some(
          TestCaseInfo(bundleName, sourceCodeVersion, Seq(
            AffectedFile("TestCaseEntry.java", Seq(
              AffectedMethod("TestCaseEntry()"),
              AffectedMethod("TestCase1()"),
              AffectedMethod("DoubleNumIfMoreThan5(int)"),
              AffectedMethod("main(String[])"))),
            AffectedFile("SharedLib.java", Seq(
              AffectedMethod("DoubleNumber(Integer)"),
              AffectedMethod("CommonLogInfo(Object)"))))))
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
