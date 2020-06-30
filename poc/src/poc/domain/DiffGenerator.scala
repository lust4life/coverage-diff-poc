package poc.domain

import java.io.InputStream

trait DiffGenerator {
  /**
   * 解析 diff，生成 diff-result
   *
   * @return
   */
  def parse(in: InputStream): Seq[DiffFile]
}


sealed trait FileStatus
final case class Created(filePath: String) extends FileStatus
final case class Deleted(filePath: String) extends FileStatus
final case class Changed(filePath: String) extends FileStatus
final case class Rename(from:String, to:String) extends FileStatus

final case class DiffFile(fileStatus: FileStatus,changedLines: Set[Double])
