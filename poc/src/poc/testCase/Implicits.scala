package poc.testCase

object Implicits extends Implicits

trait Implicits {

  implicit class MergedTestCaseInfo(self: Seq[TestCaseInfo]) {
    def merged = {
      self.groupBy(_.id).view.mapValues(_.reduce(_ + _)).values
    }
  }
}
