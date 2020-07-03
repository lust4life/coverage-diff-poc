package poc.codeBlock

class CodeBlockGeneratorByFilePath(codeBlockGenerator: CodeBlockGenerator, filePathResolver: CodeBlockFilePathResolver) {
  def generate(filePath: String): Either[CodeBlockGenerator.Error, CodeBlockGenerator.Success] = {
    codeBlockGenerator.generate(filePathResolver.getStream(filePath), filePath)
  }
}
