package poc.example

import java.nio.file.Path

import poc.fromResource
import poc.example.Implicits._
import poc.testCase.TestCaseChangedInfo
import utest._

object ShowcaseSpringBootTest extends TestSuite {

  runTestCase()

  val tests = Tests {
    "after run test case we should get jacoco data and test case info" - {
      // then
      val jacocoDataKeys = ShowcaseSpringBoot.testCaseJacocoDataStore.keys.toSeq.sorted
      jacocoDataKeys ==> Seq(1, 2, 3)

      val testCaseIds = ShowcaseSpringBoot.testCaseMemoryStore.store.map(_.id).sorted
      testCaseIds ==> Seq("test-case-1", "test-case-2", "test-case-3")
    }

    "export coverage after run test case should generate coverage files" - {
      // when
      ShowcaseSpringBoot.exportCoverage()

      // then
      assert(ShowcaseSpringBoot.memoryReportOutput.files.nonEmpty)
    }

    "show coverage changed info should report affected test cases between two code changes" - {
      // when
      val jsonStr = ShowcaseSpringBoot.showCoverageChangedInfo("spring-boot-baseline", "spring-change-shared-lib")

      // then
      val changedInfo = upickle.default.read[Seq[TestCaseChangedInfo]](jsonStr)
      assert(changedInfo.nonEmpty)
    }

  }

  def runTestCase() = {
    val mockServiceJarLocation = Path.of(fromResource("aService-0.0.1-SNAPSHOT.jar").getFile)
    val tcpServerPort = 20201
    val svcProcess = utils.startJacocoAgent(mockServiceJarLocation, tcpServerPort)
    // wait for service start
    Thread.sleep(5000)
    ShowcaseSpringBoot.runTestCase("localhost", 8080, tcpServerPort)
    svcProcess.close()
  }
}
