package poc.domain

import java.io.InputStream

sealed trait DiffFile

final case class Created(filePath: String, language: String) extends DiffFile

final case class Deleted(filePath: String, language: String) extends DiffFile

final case class Changed(filePath: String, language: String, changedLines: Set[Double]) extends DiffFile

final case class Rename(from: String, to: String, language: String, changedLines: Set[Double]) extends DiffFile

trait DiffGenerator {
  /**
   * 解析 diff，生成 diff-result
   *
   * @return
   */
  def parse(in: InputStream): Seq[DiffFile]
}
