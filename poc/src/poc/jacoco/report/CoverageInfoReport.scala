package poc.jacoco.report

import org.jacoco.core.analysis.{Analyzer, CoverageBuilder}
import org.jacoco.core.data.{ExecutionDataStore, SessionInfoStore}
import org.jacoco.report.IReportVisitor

/**
 * export execution data into coverage info format
 */
trait CoverageInfoReport extends IReportVisitor {
  def test(execDataStore: ExecutionDataStore, sessionInfoStore: SessionInfoStore) = {
    val coverageBuilder = new CoverageBuilder()
    val analyzer = new Analyzer(execDataStore, coverageBuilder)
//    analyzer.analyzeAll("zip file for java class, or some zip file stream")

    val bundleCoverage = coverageBuilder.getBundle("bundle name")

    val report: IReportVisitor = ???
    report.visitInfo(sessionInfoStore.getInfos, execDataStore.getContents)
//    report.visitBundle(bundleCoverage, "source file locator")
    report.visitEnd()
  }
}
