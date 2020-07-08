package poc.diff.jsonGenerator

import java.io.InputStream

import poc.diff.{DiffFile, DiffGenerator}


class DiffGeneratorByUnifiedDiff(diffJsonParser: DiffGeneratorByDiffJson = new DiffGeneratorByDiffJson) extends DiffGenerator {
  /**
   * 解析 diff，生成 diff-result
   *
   * @return
   */
  override def generate(in: InputStream): Seq[DiffFile] = {
    diffJsonParser.generate(parseUnifiedDiff(in))
  }

  def parseUnifiedDiff(in: InputStream): InputStream = {
    val sub =
      os.proc("diff2html", "-f", "json", "-o", "stdout", "-i", "stdin")
        .spawn(stdin = in)

    sub.stdout
  }
}
