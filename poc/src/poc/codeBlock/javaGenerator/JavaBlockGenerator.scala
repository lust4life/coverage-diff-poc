package poc.codeBlock
package javaGenerator

import java.io.InputStream

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.body.MethodDeclaration
import poc.codeBlock.CodeBlockGenerator._
import poc.codeBlock.javaGenerator.Implicits._

import scala.jdk.CollectionConverters._
import scala.jdk.StreamConverters._


class JavaBlockGenerator(parser: JavaParser) extends CodeBlockGenerator {

  def this() = this(new JavaParser())

  override def generate(in: InputStream, filePath: String): GenerateResult = {
    try {
      val parseRes = parser.parse(in)
      if (parseRes.isSuccessful) {
        val methodBlocks = parseRes.getResult.get
          .findAll(classOf[MethodDeclaration])
          .stream()
          .map(method => method.getMethodBlockOrThrow(filePath))
          .toScala(Seq)

        Right(Success(filePath, methodBlocks))
      } else {
        val reason = parseRes.getProblems.asScala
          .headOption.map(x => x.getMessage)
          .getOrElse("java parser parse failed.")
        Left(Error(filePath, reason))
      }
    } catch {
      case ex: Throwable =>
        Left(Error(filePath, ex.getMessage))
    }
  }
}