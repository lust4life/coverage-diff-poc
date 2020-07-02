package poc.diff

trait DiffFilter {
  def diffFilter: DiffFile => Boolean
}

trait FilterJavaDiff extends DiffFilter {
  override def diffFilter: DiffFile => Boolean = _.isJava
}