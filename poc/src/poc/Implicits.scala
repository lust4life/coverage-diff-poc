package poc

import scala.reflect._

object Implicits extends Implicits

trait Implicits {

  implicit class SeqWrapper(self: Seq[_]) {

    def cast[T: ClassTag] = {
      self.map({
        case x: T => x
        case x => throw new ClassCastException(s"${x.getClass.getName} can't cast to ${classTag[T].runtimeClass.getName}")
      })
    }

    def ofType[T: ClassTag] = {
      self.collect({
        case x: T => x
      })
    }
  }

  def memorize[Input, Output](f: Input => Output): Input => Output = {
    val cache = scala.collection.mutable.Map.empty[Input, Output]

    input => {
      cache.getOrElseUpdate(input, {
        f(input)
      })
    }
  }
}

