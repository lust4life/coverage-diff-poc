package poc.diff

import poc.codeBlock.BlockRange

sealed trait DiffFile {
  def language: String

  def isJava = "java".equalsIgnoreCase(language)

  val changedLines: Set[Double]

  val compactChangeLines = {
    changedLines
  }

  def contains(range: BlockRange) ={
    ???
  }
}

final case class Created(filePath: String, language: String) extends DiffFile {
  /**
   * no need changed lines info
   * @return
   */
  override val changedLines: Set[Double] = Set.empty
}

final case class Deleted(filePath: String, language: String) extends DiffFile {

  /**
   * no need changed lines info
   *
   * @return
   */
  override val changedLines: Set[Double] = Set.empty

}

final case class Changed(filePath: String, language: String, changedLines: Set[Double]) extends DiffFile

final case class Rename(from: String, to: String, language: String, changedLines: Set[Double]) extends DiffFile
