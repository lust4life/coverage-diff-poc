package poc.diff

import java.io.InputStream

trait DiffGenerator {
  /**
   * 解析 diff，生成 diff-result
   *
   * @return
   */
  def generate(in: InputStream): Seq[DiffFile]
}
