package poc.testCase

import poc.diff._

import scala.concurrent.Future

trait TestCaseResolverByDiff {
  self: TestCaseResolver with DiffFilter =>

  def retrieveByDiffs(diffs: Seq[DiffFile]): Future[Seq[TestCaseInfo]] = {
    retrieve(
      diffs
        .filter(diffFilter)
        .map({
          case Created(filePath, _) => filePath
          case Deleted(filePath, _) => filePath
          case Changed(filePath, _, _) => filePath
          case Rename(from, _, _, _) => from
        }))
  }
}
