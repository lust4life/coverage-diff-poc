package poc.testCase.jacoco

import org.jacoco.core.data.{ExecutionDataReader, ExecutionDataStore, SessionInfoStore}
import poc.Implicits.fromResource
import poc.jacoco.JacocoUtils
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

    "generate test case info from jacoco coverage info" - {
      val jarFileUrl = fromResource("mockservice.jar")
      val jarFilePath = jarFileUrl.getPath
      val jarFileStream = jarFileUrl.openStream()
      val bundleName = "test case 1 bundle"
      val bundle = JacocoUtils.analyzeCoverage(bundleName, jarFilePath, jarFileStream, execDataStore)
      val sourceCodeVersion = "some code version"
      val testCaseInfo = report.generateTestCaseInfo(sourceCodeVersion, bundle)

      testCaseInfo ==>
        Some(
          TestCaseInfo(bundleName, sourceCodeVersion, Seq(
            AffectedFile("poc/example/javacode/TestCaseEntry.java", Seq(
              AffectedMethod("poc.example.javacode.TestCaseEntry\t#\tTestCaseEntry()"),
              AffectedMethod("poc.example.javacode.TestCaseEntry\t#\tTestCase1()"),
              AffectedMethod("poc.example.javacode.TestCaseEntry\t#\tDoubleNumIfMoreThan5(int)"),
              AffectedMethod("poc.example.javacode.TestCaseEntry\t#\tmain(String[])"))),
            AffectedFile("poc/example/javacode/some/inner/folder/SharedLib.java", Seq(
              AffectedMethod("poc.example.javacode.some.inner.folder.SharedLib\t#\tDoubleNumber(Integer)"),
              AffectedMethod("poc.example.javacode.some.inner.folder.SharedLib\t#\tCommonLogInfo(Object)"))))))
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
