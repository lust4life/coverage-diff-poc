package poc

object Implicits extends Implicits

trait Implicits {

  implicit class SeqWrapper(self: Seq[_]) {

    def cast[T] = {
      self.map(_.asInstanceOf[T])
    }

    def ofType[T] = {
      self.collect({
        case x: T => x
      })
    }
  }

  def memorize[Input, Output](f: Input => Output): Input => Output = {
    val cache = new scala.collection.mutable.HashMap[Input, Output]()

    input => {
      cache.getOrElseUpdate(input, {
        f(input)
      })
    }
  }
}

