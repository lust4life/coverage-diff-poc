package poc.jacoco.report

import java.io.InputStream

import org.jacoco.core.analysis.{Analyzer, CoverageBuilder, IBundleCoverage}
import org.jacoco.core.data.{ExecutionData, ExecutionDataStore, SessionInfo}
import org.jacoco.report.{IReportVisitor, ISourceFileLocator}
import poc.store.CoverageInfo

/**
 * export execution data into coverage info format
 */
class CoverageInfoReport {
  def analyzeCoverage(bundleName: String,
                      jarLocation: String,
                      jarInputStream: InputStream,
                      execDataStore: ExecutionDataStore): IBundleCoverage = {
    val coverageBuilder = new CoverageBuilder()
    val analyzer = new Analyzer(execDataStore, coverageBuilder)
    analyzer.analyzeAll(jarInputStream, jarLocation)
    coverageBuilder.getBundle(bundleName)
  }

  def generateCoverage(): CoverageInfo = {
    ???
  }
}

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