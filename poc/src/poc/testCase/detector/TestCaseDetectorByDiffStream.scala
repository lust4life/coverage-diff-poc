package poc.testCase.detector

import java.io.InputStream

import poc.diff.DiffGenerator
import poc.testCase.{TestCaseChangedInfo, TestCaseDetector}

import scala.concurrent.Future

class TestCaseDetectorByDiffStream(diffGenerator: DiffGenerator, testCaseDetector: TestCaseDetector) {
  def detect(in: InputStream): Future[Seq[TestCaseChangedInfo]] = {
    testCaseDetector.detect(diffGenerator.generate(in))
  }
}
