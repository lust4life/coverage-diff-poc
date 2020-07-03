package poc.testCase

import poc.diff._

import scala.concurrent.Future

class TestCaseResolverByDiff(resolver: TestCaseResolver, diffFilter: DiffFilter) {

  def retrieve(diffs: Seq[DiffFile]): Future[Seq[TestCaseInfo]] = {
    resolver.retrieve(
      diffs
        .filter(diffFilter.filter)
        .map({
          case Created(filePath, _) => filePath
          case Deleted(filePath, _) => filePath
          case Changed(filePath, _, _) => filePath
          case Rename(from, _, _, _) => from
        }))
  }
}