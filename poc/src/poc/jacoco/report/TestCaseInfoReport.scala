package poc.jacoco.report

import org.jacoco.core.analysis.IBundleCoverage
import org.jacoco.core.data.SessionInfo
import org.jacoco.report.{IReportVisitor, ISourceFileLocator}

/**
 * some custom report need using source file locator
 */
abstract class CoverageInfoReportVisitor extends IReportVisitor {

  import scala.jdk.CollectionConverters._

  def generateCoverageInfo(sessionInfos: java.util.List[SessionInfo],
                           bundleCoverage: IBundleCoverage,
                           sourceFileLocator: ISourceFileLocator) = {
    val report: IReportVisitor = ???
    report.visitInfo(sessionInfos, Seq.empty.asJava)
    report.visitBundle(bundleCoverage, sourceFileLocator)
    report.visitEnd()
  }
}