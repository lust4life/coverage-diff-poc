package poc.diff

sealed trait DiffFile {
  def language: String

  def isJava = "java".equalsIgnoreCase(language)
}

final case class Created(filePath: String, language: String) extends DiffFile

final case class Deleted(filePath: String, language: String) extends DiffFile

final case class Changed(filePath: String, language: String, changedLines: Set[Double]) extends DiffFile

final case class Rename(from: String, to: String, language: String, changedLines: Set[Double]) extends DiffFile
