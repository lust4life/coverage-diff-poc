package poc.codeBlock

import poc.codeBlock.CodeBlockGenerator.Error
import poc.codeBlock.javaGenerator.JavaBlockGenerator
import utest._

object JavaGeneratorTest extends TestSuite {
  val generator = new JavaBlockGenerator()

  val tests = Tests {
    "invalid file should failed" - {
      val file = getClass.getClassLoader.getResource("Invalid.java")
      val filePath = file.getPath
      val res = generator.generate(file.openStream(), filePath)

      assertMatch(res) {
        case Left(Error(`filePath`, _)) =>
      }
    }

    'testParse - {
      val file = getClass.getClassLoader.getResource("MockJavaData.java")
      val filePath = file.getPath
      val res = generator.generate(file.openStream(), filePath)

      assert(res.isRight)

      val resSeq = res.toSeq.flatMap(_.codeBlocks)
      val methodCount = resSeq.length
      assert(methodCount == 6)

      val mockJavaData = ClassOrInterface(filePath, BlockRange(7, 27), "poc.test.somepackage.MockJavaData")

      val m1 = resSeq(0)
      assertMatch(m1) {
        case Method(`mockJavaData`, _, BlockRange(8, 9)) =>
      }

      val m2 = resSeq(1)
      assertMatch(m2) {
        case Method(`mockJavaData`, _, BlockRange(11, 13)) =>
      }

      val m3 = resSeq(2)
      assertMatch(m3) {
        case Method(`mockJavaData`, _, BlockRange(15, 15)) =>
      }

      val m4 = resSeq(3)
      assertMatch(m4) {
        case Method(`mockJavaData`, "SomeGenericMethod(DataType, Optional, Map, int, List)", BlockRange(18, 20)) =>
      }

      val m5 = resSeq(4)
      assertMatch(m5) {
        case Method(ClassOrInterface(`filePath`, BlockRange(22, 26), "poc.test.somepackage.MockJavaData.NestedClass"), _, BlockRange(23, 25)) =>
      }

      val m6 = resSeq(5)
      assertMatch(m6) {
        case Method(ClassOrInterface(`filePath`, BlockRange(29, 32), "poc.test.somepackage.AnotherTopLevelClass"), _, BlockRange(30, 31)) =>
      }
    }
  }
}
