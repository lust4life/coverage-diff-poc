![Scala CI](https://github.com/lust4life/coverage-diff-poc/workflows/Scala%20CI/badge.svg)
[![codecov](https://codecov.io/gh/lust4life/coverage-diff-poc/branch/master/graph/badge.svg)](https://codecov.io/gh/lust4life/coverage-diff-poc)
====

# pre-requirement

- https://www.npmjs.com/package/diff2html-cli

  you need install diff2html cli tool, for diff parsing

# class explanation

```scala
  // only using when you using github as your code diff locator and your source code file resolver
  // other implementation are pluggable, e.g. gitlab, local git repo...
  val githubRepo = GithubRepo("lust4life", "coverage-diff-poc")

  // your test case coverage store when you generate test case info by jacoco coverage info
  val testCaseMemoryStore = new TestCaseMemoryStore()

  // test case resolver using memory store
  val testCaseResolver = new TestCaseResolverFromMemory("example/src/", testCaseMemoryStore)

  // find test cases which affected in diff's changed/renamed/... files
  val testCaseResolverByDiff = new TestCaseResolverByDiff(testCaseResolver, JavaDiffFilter)

  // using java parser to generate java code structure, using for detect changes by diff lines.
  val javaCodeBlockGenerator = new JavaBlockGenerator()

  // how to find source code, here using github raw file url
  val codeBlockFilePathResolverFromGithub = new CodeBlockFilePathResolverFromGithub(githubRepo, "master")
  
  // link generator with file resolver
  val codeBlockGeneratorByFilePath = new CodeBlockGeneratorByFilePath(javaCodeBlockGenerator, codeBlockFilePathResolverFromGithub)

  // using detector to find affected test cases by compare diff changed lines and source code method structure
  val testCaseDetector = new TestCaseDetectorByCodeBlock(codeBlockGeneratorByFilePath, testCaseResolverByDiff)

  // diff stream locator from github
  val diffStreamLocatorFromGithub = new DiffStreamLocatorFromGithub(githubRepo)

  // using diff2html to parse unified diffs
  val diffParser = new DiffParserByUnifiedDiff()
```

# find test case affected by code changes

- start your service with jacoco javaagent using tcpserver as the output

    - https://www.eclemma.org/jacoco/trunk/doc/agent.html
    
    see `utils.startJacocoAgent` in [utils.scala](./example/src/poc/example/utils.scala)

- run your test case automatically or manually, then using `jacocoClient.grab()` or `jacocoClient.grabAndReset()` to get jacoco execution data

    you can save execution data in file for later report generating, or your can generate test case coverage info from this execution data.

    ```scala
    val (_, execData) = utils.jacocoClient.grab()

    // generate test case info from jacoco coverage and save it into memory db
    val testCaseInfoFromJacoco = new TestCaseInfoFromJacoco()
    val testCaseName = "your test case name"
    val jarLocation = "your service jar location path, only for helper message when error"
    val jarFileStream = "your service jar file stream"
   
    // analyze jacoco execution data, then generate test case info we needed
    val bundle = testCaseInfoFromJacoco.analyzeCoverage(testCaseName, jarLocation, jarFileStream, execData)
    testCaseInfoFromJacoco.generateTestCaseInfo("master", bundle)
      .map(testCaseMemoryStore.save)
      .foreach(Await.result(_, Duration.Inf))
    ```
   
- after generate the test case coverage info, you can find which test case affected by code changes through diff (commits or pull request)
   
   ```scala
    val diffStream = diffStreamLocatorFromGithub.getDiffStreamByPullRequest(pull-request-id)
    detectByDiffStream(diffStream)
  
    // or by commits
    val diffStream = diffStreamLocatorFromGithub.getDiffStream(base, target)
    detectByDiffStream(diffStream)
  
    // using diff parser to parse unified diff and then using java parser to parse source code structure
    // then detect affected test cases
    private def detectByDiffStream(diffStream: InputStream) = {
      val diffFiles = diffParser.parse(diffStream)
      val changedInfos = Await.result(testCaseDetector.detect(diffFiles), Duration.Inf)
    }
    ```
   
# other resources

- mill build for source code
    - http://www.lihaoyi.com/mill/
