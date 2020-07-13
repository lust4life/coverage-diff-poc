package poc.diff
package parser

import java.io.InputStream


class DiffParserByUnifiedDiff(diffJsonParser: DiffParserByDiffJson = new DiffParserByDiffJson) extends DiffParser {
  /**
   * 解析 diff，生成 diff-result
   *
   * @return
   */
  override def parse(in: InputStream): Seq[DiffFile] = {
    diffJsonParser.parse(parseUnifiedDiff(in))
  }

  def parseUnifiedDiff(in: InputStream): InputStream = {
    val sub =
      os.proc("diff2html", "-f", "json", "-o", "stdout", "-i", "stdin")
        .spawn(stdin = in)

    sub.stdout
  }
}