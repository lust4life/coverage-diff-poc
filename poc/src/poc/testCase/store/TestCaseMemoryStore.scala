package poc.testCase.store

import poc.testCase.{TestCaseInfo, TestCaseStore}

import scala.async.Async._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class TestCaseMemoryStore extends TestCaseStore {
  val store = scala.collection.mutable.Map[String, TestCaseInfo]()

  override def save(coverageInfo: TestCaseInfo): Future[Unit] = async {
    store.update(s"${coverageInfo.id}-${coverageInfo.sourceCodeVersion}", coverageInfo)
  }
}
