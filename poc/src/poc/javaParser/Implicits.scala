package poc.javaParser
import java.util.Optional

import com.github.javaparser.Range
import com.github.javaparser.ast.body.{ClassOrInterfaceDeclaration, MethodDeclaration}
import poc.domain.BlockRange

object Implicits extends Implicits

trait Implicits {

  implicit def apply(r: Range) = {
    BlockRange(r.begin.line, r.end.line)
  }

  implicit class ShortcutForClassOrInterface(self: ClassOrInterfaceDeclaration) {
    def fullyQualifiedName = {
      self.getFullyQualifiedName.orElseThrow(() => {
        throw new Exception(s"${self.getNameAsString}'s fully qualified name is empty")
      })
    }

    def getRangeOrThrow(implicit filePath: String) = {
      self.getRange.orElseThrow(() => {
        throw new Exception(s"${filePath} => ${self.getFullyQualifiedName} doesn't have range info")
      })
    }
  }

  implicit class ShortcutForMethod(self: MethodDeclaration) {
    def getRangeOrThrow(implicit filePath: String) = {
      self.getRange.orElseThrow(() => {
        throw new Exception(s"${filePath} => ${self.getDeclarationAsString()} doesn't have range info")
      })
    }

    def getContainerOrThrow(implicit filePath: String) ={
      self.findAncestor(classOf[ClassOrInterfaceDeclaration]).orElseThrow(() => {
        throw new Exception(s"${filePath} => ${self.getDeclarationAsString()} method could not find its class or interface")
      })
    }
  }

}