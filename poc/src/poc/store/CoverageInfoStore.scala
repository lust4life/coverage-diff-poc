package poc.store

import scala.concurrent.Future
import scala.util.Try

trait CoverageInfoStore {

  def save(coverageInfo: CoverageInfo): Future[Try[Unit]]

}

