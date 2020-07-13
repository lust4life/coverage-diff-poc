package poc.testCase.store

import poc.testCase.{TestCaseInfo, TestCaseStore}

import scala.concurrent.Future
import scala.util.Try
import scala.async.Async._
import scala.concurrent.ExecutionContext.Implicits.global


class TestCaseMemoryStore extends TestCaseStore {
  val store = scala.collection.mutable.Seq.empty

  override def save(coverageInfo: TestCaseInfo): Future[Try[Unit]] = async {
    Try {
      coverageInfo +: store
    }
  }
}
