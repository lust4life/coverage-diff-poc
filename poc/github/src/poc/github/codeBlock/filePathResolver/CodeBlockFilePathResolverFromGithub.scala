package poc.github
package codeBlock.filePathResolver

import java.io.{ByteArrayInputStream, InputStream}

import poc.codeBlock.CodeBlockFilePathResolver


class CodeBlockFilePathResolverFromGithub(githubRepo: GithubRepo, commit: String) extends CodeBlockFilePathResolver {
  override def getStream(filePath: String): InputStream = {
    val response = requests.get(githubRepo.getRawFileUrl(commit, filePath))
    new ByteArrayInputStream(response.bytes)
  }
}
