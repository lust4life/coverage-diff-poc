package poc.example

import java.io.{ByteArrayOutputStream, InputStream}
import java.net.Socket
import java.nio.file.Path

import org.jacoco.core.data.{ExecutionDataStore, SessionInfoStore}
import org.jacoco.report.DirectorySourceFileLocator
import poc.codeBlock.CodeBlockGeneratorByFilePath
import poc.codeBlock.filePathResolver.CodeBlockFilePathResolverByLocalFile
import poc.codeBlock.javaGenerator.JavaBlockGenerator
import poc.diff.JavaDiffFilter
import poc.diff.parser.DiffParserByUnifiedDiff
import poc.diff.streamLocator.DiffStreamLocatorFromGit
import poc.example.Implicits._
import poc.fromResource
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
  override def port: Int = 8090

  override def host: String = "0.0.0.0"

  val jarLocationPath: Path = Path.of(sys.env.getOrElse("aServiceJarLocation", fromResource("aService-0.0.1-SNAPSHOT.jar").getFile))

  def classesInputStream: InputStream = utils.chooseClassDirInSpringBootJar(jarLocationPath)

  val gitTool: GitTool = GitTool(os.temp.dir(prefix = "show-case-git-dir").toIO, "https://github.com/lust4life/coverage-diff-poc")
  val diffParser = new DiffParserByUnifiedDiff()
  val diffStreamLocatorFromGit = new DiffStreamLocatorFromGit(gitTool)

  val testCaseMemoryStore = new TestCaseMemoryStore()
  val testCaseResolver = new TestCaseResolverFromMemory("spring-boot-service-demo/aService/src/main/java", testCaseMemoryStore)
  val testCaseResolverByDiff = new TestCaseResolverByDiff(testCaseResolver, JavaDiffFilter)

  val javaCodeBlockGenerator = new JavaBlockGenerator()
  val codeBlockFilePathResolver = new CodeBlockFilePathResolverByLocalFile(gitTool.repoDir)
  val codeBlockGeneratorByFilePath = new CodeBlockGeneratorByFilePath(javaCodeBlockGenerator, codeBlockFilePathResolver)

  val testCaseDetector = new TestCaseDetectorByCodeBlock(codeBlockGeneratorByFilePath, testCaseResolverByDiff)

  var memoryReportOutput = new MemoryMultiReportOutput
  var testCaseJacocoDataStore: Map[Int, (SessionInfoStore, ExecutionDataStore)] = Map[Int, (SessionInfoStore, ExecutionDataStore)]()

  @memoryFiles("/coverage")
  def coverage(): Map[String, ByteArrayOutputStream] = memoryReportOutput.files.toMap

  @cask.get("/export-coverage")
  def exportCoverage(): String = {
    // merge all test case execution data
    val (mergedSessionStore, mergedExecutionStore) = (new SessionInfoStore, new ExecutionDataStore)
    testCaseJacocoDataStore.values.foreach {
      case (eachSession, eachExecData) =>
        eachSession.accept(mergedSessionStore)
        eachExecData.accept(mergedExecutionStore)
    }

    // git clone code into some directory
    gitTool.ensureRepoCloned()

    val sourceDir = Path.of(gitTool.repoDir.getPath, "spring-boot-service-demo/aService/src/main/java/")
    val sourceFileLocator = new DirectorySourceFileLocator(sourceDir.toFile, "UTF-8", 2)
    val tmpMemoryOutput = new MemoryMultiReportOutput
    JacocoUtils.exportToHtml("whole-coverage",
      tmpMemoryOutput,
      mergedSessionStore,
      mergedExecutionStore,
      jarLocationPath.toString,
      classesInputStream,
      sourceFileLocator
    )

    memoryReportOutput = tmpMemoryOutput
    "Done"
  }

  @cask.get("/run-test-case-automatically/:host/:port/:tcpPort")
  def runTestCase(host: String, port: Int, tcpPort: Int): String = {
    // grab jacoco data from mock service then generate test case info

    val jacocoClient = new JacocoClient(
      new Socket(host, tcpPort)
    )
    val someServiceUrl = s"http://$host:$port"

    jacocoClient.reset()

    val testCaseIds = Seq(1, 2, 3)
    testCaseIds.foreach(caseId => {
      val res = requests.get(s"$someServiceUrl/run-testcase/$caseId")
      if (res.is2xx) {
        val jacocoData = jacocoClient.grabAndReset()
        testCaseJacocoDataStore += (caseId -> jacocoData)
      }
    })

    generateTestCaseInfoFromJacoco(testCaseMemoryStore, testCaseJacocoDataStore)

    upickle.default.write(testCaseMemoryStore.store.values.toSeq, 2)
  }

  private def generateTestCaseInfoFromJacoco(testCaseStore: TestCaseStore,
                                             testCaseJacocoDataStore: Map[Int, (SessionInfoStore, ExecutionDataStore)]) = {
    val testCaseInfoFromJacoco = new TestCaseInfoFromJacoco()

    testCaseJacocoDataStore.foreach {
      case (caseId, (_, execData)) =>
        val testCaseName = s"test-case-$caseId"
        val bundle = JacocoUtils.analyzeCoverage(testCaseName, jarLocationPath.toString, classesInputStream, execData)
        testCaseInfoFromJacoco.generateTestCaseInfo("master", bundle)
          .map(testCaseStore.save)
          .foreach(Await.result(_, Duration.Inf))
    }
  }

  @cask.get("/reset/:host/:tcpPort")
  def reset(host: String, tcpPort: Int) = {
    val jacocoClient = new JacocoClient(
      new Socket(host, tcpPort)
    )
    jacocoClient.reset()
  }

  @cask.get("/grab/:host/:tcpPort")
  def grab(host: String, tcpPort: Int, caseId: Int): String = {
    val jacocoClient = new JacocoClient(
      new Socket(host, tcpPort)
    )

    val jacocoData = jacocoClient.grabAndReset()
    testCaseJacocoDataStore += (caseId -> jacocoData)

    generateTestCaseInfoFromJacoco(testCaseMemoryStore, testCaseJacocoDataStore)

    upickle.default.write(testCaseMemoryStore.store.values.toSeq, 2)
  }

  @cask.get("/show-coverage-changed-info/compare")
  def showCoverageChangedInfo(base: String, target: String): String = {
    gitTool.ensureRepoCloned()
    gitTool.resetTo(target)

    val diffStream = diffStreamLocatorFromGit.getDiffStream(base, target)
    val diffFiles = diffParser.parse(diffStream)
    val changedInfos = Await.result(testCaseDetector.detect(diffFiles), Duration.Inf)
    upickle.default.write(changedInfos, 2)
  }


  initialize()
}
