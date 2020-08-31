package poc.testCase.jacoco

import poc.Implicits.fromResource
import poc.jacoco.JacocoUtils
import poc.testCase.{AffectedFile, AffectedMethod, TestCaseInfo}
import poc.test.utils
import utest._

object TestCaseInfoFromJacocoTest extends TestSuite {
  val report = new TestCaseInfoFromJacoco()

  val tests = Tests {

    "generate test case info from jacoco coverage info" - {
      val (_, execDataStore) = utils.getMockCoverageExecData()
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
              AffectedMethod("poc.example.javacode.TestCaseEntry#TestCaseEntry()"),
              AffectedMethod("poc.example.javacode.TestCaseEntry#TestCase1()"),
              AffectedMethod("poc.example.javacode.TestCaseEntry#DoubleNumIfMoreThan5(int)"),
              AffectedMethod("poc.example.javacode.TestCaseEntry#main(String[])"))),
            AffectedFile("poc/example/javacode/some/inner/folder/SharedLib.java", Seq(
              AffectedMethod("poc.example.javacode.some.inner.folder.SharedLib#DoubleNumber(Integer)"),
              AffectedMethod("poc.example.javacode.some.inner.folder.SharedLib#CommonLogInfo(Object)"))))))
    }
  }


}
