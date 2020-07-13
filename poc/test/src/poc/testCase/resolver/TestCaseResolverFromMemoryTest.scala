package poc.testCase.resolver

import poc.testCase.{AffectedFile, TestCaseInfo}
import poc.testCase.store.TestCaseMemoryStore
import utest._

import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global

object TestCaseResolverFromMemoryTest extends TestSuite {


  val tests = Tests {
    val testCaseStore = new TestCaseMemoryStore()
    val resolver = new TestCaseResolverFromMemory("", testCaseStore)

    "get test case from memory store" - async {
      val testCase1 = TestCaseInfo("1", "some version", Seq(
        AffectedFile("a.java", Seq()),
        AffectedFile("b.java", Seq())))

      val testCase2 = TestCaseInfo("2", "some version", Seq(
        AffectedFile("b.java", Seq()),
        AffectedFile("c.java", Seq())))

      // given
      await {
        testCaseStore.save(testCase1)
      }

      await {
        testCaseStore.save(testCase2)
      }

      // when
      val testCaseInfos = await {
        resolver.retrieve("b.java")
      }
      testCaseInfos.length ==> 2

      val testCaseInfosByBatch = await {
        resolver.retrieve(Seq("a.java", "c.java"))
      }

      testCaseInfosByBatch.length ==> 2
    }
  }
}
