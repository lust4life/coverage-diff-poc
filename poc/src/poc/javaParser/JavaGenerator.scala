package poc.javaParser

import java.io.InputStream

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.body.MethodDeclaration
import poc.domain._
import poc.javaParser.Implicits._

import scala.jdk.CollectionConverters._
import scala.jdk.StreamConverters._
import scala.util.Try

class JavaGenerator(parser: JavaParser) extends StructureGenerator {

  def this() = this(new JavaParser())

  override def parse(in: InputStream, filePath: String): Try[Seq[CodeBlock]] = {
    Try {
      val parseRes = parser.parse(in)
      if (parseRes.isSuccessful) {
        parseRes.getResult.get
          .findAll(classOf[MethodDeclaration])
          .stream()
          .map(method => method.getMethodBlockOrThrow(filePath))
          .toScala(Seq)
      } else {
        val error = parseRes.getProblems.asScala
          .headOption.map(x => x.getMessage)
          .getOrElse("java parser parse failed.")
        throw new Exception(error)
      }
    }
  }
}