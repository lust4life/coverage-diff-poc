package poc.example

import java.io.InputStream
import java.net.Socket
import java.nio.file.Path

import poc.example.Implicits._
import poc.codeBlock.CodeBlockGeneratorByFilePath
import poc.codeBlock.javaGenerator.JavaBlockGenerator
import poc.diff.{Changed, JavaDiffFilter}
import poc.diff.parser.DiffParserByUnifiedDiff
import poc.fromResource
import poc.github.GithubRepo
import poc.github.codeBlock.filePathResolver.CodeBlockFilePathResolverFromGithub
import poc.github.diff.streamLocator.DiffStreamLocatorFromGithub
import poc.jacoco.{JacocoClient, JacocoUtils}
import poc.testCase._
import poc.testCase.detector.TestCaseDetectorByCodeBlock
import poc.testCase.jacoco.TestCaseInfoFromJacoco
import poc.testCase.resolver.TestCaseResolverFromMemory
import poc.testCase.store.TestCaseMemoryStore

import scala.concurrent.Await
import scala.concurrent.duration.Duration


object Showcase extends cask.MainRoutes {
  val githubRepo = GithubRepo("lust4life", "coverage-diff-poc")

  val testCaseMemoryStore = new TestCaseMemoryStore()
  val testCaseResolver = new TestCaseResolverFromMemory("example/src/", testCaseMemoryStore)
  val testCaseResolverByDiff = new TestCaseResolverByDiff(testCaseResolver, JavaDiffFilter)
  val javaCodeBlockGenerator = new JavaBlockGenerator()
  val codeBlockFilePathResolverFromGithub = new CodeBlockFilePathResolverFromGithub(githubRepo, "master")
  val codeBlockGeneratorByFilePath = new CodeBlockGeneratorByFilePath(javaCodeBlockGenerator, codeBlockFilePathResolverFromGithub)

  val testCaseDetector = new TestCaseDetectorByCodeBlock(codeBlockGeneratorByFilePath, testCaseResolverByDiff)
  val diffStreamLocatorFromGithub = new DiffStreamLocatorFromGithub(githubRepo)
  val diffParser = new DiffParserByUnifiedDiff()


  @cask.get("/run-test-case/:id")
  def runTestCase(id: Int) = {
    // simulate run some test case in mock service
    val tcpServerPort = 20202
    val mockServiceJarLocation = Path.of(fromResource("mockservice.jar").getFile)
    val sub = utils.startJacocoAgent(mockServiceJarLocation, tcpServerPort)
    sub.stdout.readLine()
    sub.stdin.writeLine(id.toString)
    sub.stdin.flush()

    // grab jacoco data from mock service
    val (_, execData) = new JacocoClient(
      new Socket("localhost", tcpServerPort)
    ).grabAndReset()

    // generate test case info from jacoco coverage and save it into memory db
    val testCaseInfoFromJacoco = new TestCaseInfoFromJacoco()
    val testCaseName = s"test-case-$id"
    val jarLocation = utils.mockServiceUrl.getPath
    val jarFileStream = utils.mockServiceUrl.openStream()
    val bundle = JacocoUtils.analyzeCoverage(testCaseName, jarLocation, jarFileStream, execData)
    testCaseInfoFromJacoco.generateTestCaseInfo("master", bundle)
      .map(testCaseMemoryStore.save)
      .foreach(Await.result(_, Duration.Inf))

    // output result
    sub.stdin.writeLine("overZ")
    sub.stdin.flush()
    sub.close()

    upickle.default.write(testCaseMemoryStore.store, 2)
  }

  @cask.get("/show-coverage-changed-info/pull/:id")
  def showCoverageChangedInfo(id: Int) = {
    val diffStream = diffStreamLocatorFromGithub.getDiffStreamByPullRequest(id)
    detectByDiffStream(diffStream)
  }

  @cask.get("/show-coverage-changed-info/compare/:base/:target")
  def showCoverageChangedInfo(base: String, target: String) = {
    val diffStream = diffStreamLocatorFromGithub.getDiffStream(base, target)
    detectByDiffStream(diffStream)
  }

  private def detectByDiffStream(diffStream: InputStream) = {
    val diffFiles = diffParser.parse(diffStream)
    val changedInfos = Await.result(testCaseDetector.detect(diffFiles), Duration.Inf)
    upickle.default.write(changedInfos, 2)
  }

  initialize()
}
