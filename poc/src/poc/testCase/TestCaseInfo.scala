package poc.testCase

/**
 * test case coverage info from db or elsewhere,
 * in order to detect changed info
 *
 * @param id       test case identity
 * @param coverage {filePath : ["method1","method2"...]}
 */
final case class TestCaseInfo(id: String, coverage: Map[String, Set[String]])
