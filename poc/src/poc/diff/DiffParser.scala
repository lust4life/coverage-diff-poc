package poc.diff

import java.io.InputStream

trait DiffParser {
  /**
   * 解析 diff，生成 diff-result
   *
   * @return
   */
  def parse(in: InputStream): Seq[DiffFile]
}
