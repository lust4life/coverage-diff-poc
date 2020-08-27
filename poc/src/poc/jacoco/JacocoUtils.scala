package poc.jacoco

import java.io.InputStream

import org.jacoco.core.analysis.{Analyzer, CoverageBuilder, IBundleCoverage}
import org.jacoco.core.data.{ExecutionDataStore, SessionInfoStore}
import org.jacoco.report.html.HTMLFormatter
import org.jacoco.report.{IMultiReportOutput, ISourceFileLocator}

/**
 * utils for export html
 */
object JacocoUtils {

  def exportToHtml(bundleName: String,
                   reportOutput: IMultiReportOutput,
                   sessionInfoStore: SessionInfoStore,
                   executionDataStore: ExecutionDataStore,
                   jarLocation: String,
                   jarInputStream: InputStream,
                   sourceFileLocator: ISourceFileLocator) = {
    val bundleCoverage = analyzeCoverage(bundleName, jarLocation, jarInputStream, executionDataStore)
    val htmlFormatter = new HTMLFormatter()
    val reportVisitor = htmlFormatter.createVisitor(reportOutput)
    reportVisitor.visitInfo(sessionInfoStore.getInfos, executionDataStore.getContents)
    reportVisitor.visitBundle(bundleCoverage, sourceFileLocator)
    reportVisitor.visitEnd()
  }

  def analyzeCoverage(bundleName: String,
                      jarLocation: String,
                      jarInputStream: InputStream,
                      execDataStore: ExecutionDataStore): IBundleCoverage = {
    val coverageBuilder = new CoverageBuilder()
    val analyzer = new Analyzer(execDataStore, coverageBuilder)
    analyzer.analyzeAll(jarInputStream, jarLocation)
    coverageBuilder.getBundle(bundleName)
  }
}