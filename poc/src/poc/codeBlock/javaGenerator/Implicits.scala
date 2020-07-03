package poc.codeBlock
package javaGenerator

import com.github.javaparser.Range
import com.github.javaparser.ast.body.{ClassOrInterfaceDeclaration, MethodDeclaration}

object Implicits extends Implicits

trait Implicits {

  implicit def apply(r: Range): BlockRange = {
    BlockRange(r.begin.line, r.end.line)
  }

  implicit class ShortcutForClassOrInterface(self: ClassOrInterfaceDeclaration) {
    private def getFullyQualifiedNameOrThrow(filePath: String): String = {
      self.getFullyQualifiedName.orElseThrow(() => {
        throw new Exception(s"${filePath} => ${self.getNameAsString}'s fully qualified name is empty")
      })
    }

    private def getRangeOrThrow(filePath: String): Range = {
      self.getRange.orElseThrow(() => {
        throw new Exception(s"${filePath} => ${self.getFullyQualifiedName} doesn't have range info")
      })
    }

    def getClassOrInterfaceBlockOrThrow(filePath: String): ClassOrInterface = {
      ClassOrInterface(filePath, getRangeOrThrow(filePath), getFullyQualifiedNameOrThrow(filePath))
    }
  }

  implicit class ShortcutForMethod(method: MethodDeclaration) {
    private def getRangeOrThrow(filePath: String): Range = {
      method.getRange.orElseThrow(() => {
        throw new Exception(s"${filePath} => ${method.getDeclarationAsString()} doesn't have range info")
      })
    }

    private def getContainerOrThrow(filePath: String): ClassOrInterfaceDeclaration = {
      method.findAncestor(classOf[ClassOrInterfaceDeclaration]).orElseThrow(() => {
        throw new Exception(s"${filePath} => ${method.getDeclarationAsString()} method could not find its class or interface")
      })
    }

    def getMethodBlockOrThrow(filePath: String): Method = {
      val signature = method.getSignature.asString()
      val container = getContainerOrThrow(filePath).getClassOrInterfaceBlockOrThrow(filePath)
      Method(container, signature, getRangeOrThrow(filePath))
    }
  }

}