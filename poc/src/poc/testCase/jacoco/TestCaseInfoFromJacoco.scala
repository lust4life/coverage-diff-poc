package poc.testCase
package jacoco

import java.io.InputStream

import org.jacoco.core.analysis.{Analyzer, CoverageBuilder, IBundleCoverage}
import org.jacoco.core.data.ExecutionDataStore
import org.jacoco.report.JavaNames
import poc.jacoco.Implicits._

import scala.jdk.CollectionConverters._

/**
 * export execution data into coverage info format
 */
class TestCaseInfoFromJacoco {
  private val javaNames = new JavaNames()

  def analyzeCoverage(bundleName: String,
                      jarLocation: String,
                      jarInputStream: InputStream,
                      execDataStore: ExecutionDataStore): IBundleCoverage = {
    val coverageBuilder = new CoverageBuilder()
    val analyzer = new Analyzer(execDataStore, coverageBuilder)
    analyzer.analyzeAll(jarInputStream, jarLocation)
    coverageBuilder.getBundle(bundleName)
  }

  def generateTestCaseInfo(sourceCodeVersion: String, bundle: IBundleCoverage): Option[TestCaseInfo] = {
    val bundleName = bundle.getName
    val isCovered = bundle.getMethodCounter.isCovered

    Option.when(isCovered) {
      val coveredMethods =
        bundle.getPackages.asScala
          .flatMap(_.getClasses.asScala)
          .filter(_.getMethodCounter.isCovered)
          .map(classCoverage => {
            val packageName = classCoverage.getPackageName
            val sourceFileName = packageName + "/" + classCoverage.getSourceFileName
            val methods =
              classCoverage.getMethods.asScala
                .filter(_.getMethodCounter.isCovered)
                .map(methodCoverage => {
                  val vmDesc = methodCoverage.getDesc
                  val vmSignature = methodCoverage.getSignature
                  val vmMethodName = methodCoverage.getName
                  val vmClassName = classCoverage.getName
                  val methodSignature = javaNames.getMethodName(vmClassName, vmMethodName, vmDesc, vmSignature)
                  val classSignature = javaNames.getQualifiedClassName(vmClassName)
                  AffectedMethod(classSignature, methodSignature)
                })
                .toSeq
            AffectedFile(sourceFileName, methods)
          })
          .toSeq

      TestCaseInfo(bundleName, sourceCodeVersion, coveredMethods)
    }
  }
}
