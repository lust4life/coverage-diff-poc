package poc.diff

trait DiffFilter {
  def filter: DiffFile => Boolean
}

object JavaDiffFilter extends DiffFilter {
  override def filter: DiffFile => Boolean = _.isJava
}
