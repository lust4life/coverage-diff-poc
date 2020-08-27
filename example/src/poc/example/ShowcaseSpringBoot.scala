package poc.example

import java.io.{File, InputStream}
import java.net.Socket

import org.jacoco.core.data.{ExecutionDataStore, SessionInfoStore}
import org.jacoco.report.DirectorySourceFileLocator
import poc.Implicits.fromResource
import poc.codeBlock.CodeBlockGeneratorByFilePath
import poc.codeBlock.filePathResolver.CodeBlockFilePathResolverByLocalFile
import poc.codeBlock.javaGenerator.JavaBlockGenerator
import poc.diff.JavaDiffFilter
import poc.diff.parser.DiffParserByUnifiedDiff
import poc.diff.streamLocator.DiffStreamLocatorFromGit
import poc.example.Implicits._
import poc.jacoco.{JacocoClient, JacocoUtils, MemoryMultiReportOutput}
import poc.testCase._
import poc.testCase.detector.TestCaseDetectorByCodeBlock
import poc.testCase.jacoco.TestCaseInfoFromJacoco
import poc.testCase.resolver.TestCaseResolverFromMemory
import poc.testCase.store.TestCaseMemoryStore
import poc.tools.git.GitTool

import scala.concurrent.Await
import scala.concurrent.duration.Duration


object ShowcaseSpringBoot extends cask.MainRoutes {
  val jarLocationUrl = fromResource("aService-0.0.1-SNAPSHOT.jar")
  val gitTool = GitTool("/tmp/show-case-example", "https://github.com/lust4life/coverage-diff-poc")
  val diffParser = new DiffParserByUnifiedDiff()
  val diffStreamLocatorFromGit = new DiffStreamLocatorFromGit(gitTool)

  val testCaseMemoryStore = new TestCaseMemoryStore()
  val testCaseResolver = new TestCaseResolverFromMemory("spring-boot-service-demo/aService/", testCaseMemoryStore)
  val testCaseResolverByDiff = new TestCaseResolverByDiff(testCaseResolver, JavaDiffFilter)

  val javaCodeBlockGenerator = new JavaBlockGenerator()
  val codeBlockFilePathResolver = new CodeBlockFilePathResolverByLocalFile(gitTool.repoDir)
  val codeBlockGeneratorByFilePath = new CodeBlockGeneratorByFilePath(javaCodeBlockGenerator, codeBlockFilePathResolver)

  val testCaseDetector = new TestCaseDetectorByCodeBlock(codeBlockGeneratorByFilePath, testCaseResolverByDiff)

  var memoryReportOutput = new MemoryMultiReportOutput

  @memoryFiles("/coverage")
  def staticFileRoutes() = memoryReportOutput

  @cask.post("/export-coverage")
  def exportCoverage(path: String) = {
    // merge all test case execution data
    val (mergedSessionStore, mergedExecutionStore) =
      testCaseJacocoDataStore.values.fold((new SessionInfoStore, new ExecutionDataStore)) {
        case ((mergedSession, mergedExecData), (eachSession, eachExecData)) => {
          mergedSession.accept(eachSession)
          mergedExecData.accept(eachExecData)
          (mergedSession, mergedExecData)
        }
      }

    // git clone code into some directory
    gitTool.ensureRepoCloned()

    val sourceCodeDir = new File(gitTool.repoDir)
    val sourceFileLocator = new DirectorySourceFileLocator(sourceCodeDir, "UTF-8", 2)
    val tmpMemoryOutput = new MemoryMultiReportOutput
    JacocoUtils.exportToHtml("whole-coverage",
      tmpMemoryOutput,
      mergedSessionStore,
      mergedExecutionStore,
      jarLocationUrl.getPath,
      jarLocationUrl.openStream(),
      sourceFileLocator
    )

    memoryReportOutput = tmpMemoryOutput
    "Done"
  }

  var testCaseJacocoDataStore = Map[Int, (SessionInfoStore, ExecutionDataStore)]()

  @cask.get("/run-test-case-automatically/:host/:port")
  def runTestCase(host: String, port: Int) = {
    // grab jacoco data from mock service then generate test case info

    val jacocoClient = new JacocoClient(
      new Socket(host, port)
    )
    val someServiceUrl = s"http://$host:$port"

    val testCaseIds = Seq(1, 2, 3)
    testCaseIds.foreach(caseId => {
      val res = requests.get(s"http://$someServiceUrl/run-testcase/$caseId")
      if (res.is2xx) {
        val jacocoData = jacocoClient.grabAndReset()
        testCaseJacocoDataStore += (caseId -> jacocoData)
      }
    })

    val jarLocation = jarLocationUrl.getPath
    val jarFileStream = jarLocationUrl.openStream()
    val testCaseInfoFromJacoco = new TestCaseInfoFromJacoco()

    testCaseJacocoDataStore.foreach {
      case (caseId, (_, execData)) => {
        // generate test case info from jacoco coverage and save it into memory db
        val testCaseName = s"test-case-$caseId"
        val bundle = JacocoUtils.analyzeCoverage(testCaseName, jarLocation, jarFileStream, execData)
        testCaseInfoFromJacoco.generateTestCaseInfo("master", bundle)
          .map(testCaseMemoryStore.save)
          .foreach(Await.result(_, Duration.Inf))
      }
    }

    upickle.default.write(testCaseMemoryStore.store, 2)
  }

  @cask.get("/show-coverage-changed-info/compare/:base/:target")
  def showCoverageChangedInfo(base: String, target: String) = {
    gitTool.ensureRepoCloned()

    val diffStream = diffStreamLocatorFromGit.getDiffStream(base, target)
    detectByDiffStream(diffStream)
  }

  private def detectByDiffStream(diffStream: InputStream) = {
    val diffFiles = diffParser.parse(diffStream)
    val changedInfos = Await.result(testCaseDetector.detect(diffFiles), Duration.Inf)
    upickle.default.write(changedInfos, 2)
  }

  initialize()
}
