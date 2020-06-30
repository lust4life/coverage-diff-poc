package poc.javaParser

import java.io.InputStream

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.body.MethodDeclaration
import poc.domain._
import poc.javaParser.Implicits._
import poc.domain.ParseResult._

import scala.jdk.CollectionConverters._
import scala.jdk.StreamConverters._


class JavaGenerator(parser: JavaParser) extends StructureGenerator {

  def this() = this(new JavaParser())

  override def parse(in: InputStream, filePath: String): Either[Error, Success] = {
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
      case ex =>
        Left(Error(filePath, ex.getMessage))
    }
  }
}