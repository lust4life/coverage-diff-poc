package poc

import java.io.FileNotFoundException
import java.net.URL

import scala.reflect._

object Implicits extends Implicits

trait Implicits {

  implicit class SeqWrapper(self: Seq[_]) {

    def cast[T: ClassTag]: Seq[T] = {
      self.map({
        case x: T => x
        case x => throw new ClassCastException(s"${x.getClass.getName} can't cast to ${classTag[T].runtimeClass.getName}")
      })
    }

    def ofType[T: ClassTag]: Seq[T] = {
      self.collect({
        case x: T => x
      })
    }
  }

  def memorize[Input, Output](f: Input => Output): Input => Output = {
    val cache = scala.collection.mutable.Map.empty[Input, Output]
    input => cache.getOrElseUpdate(input, f(input))
  }

  def fromResource(resource: String, classLoader: ClassLoader = Thread.currentThread().getContextClassLoader()): URL =
    Option(classLoader.getResource(resource)) match {
      case Some(url) => url
      case None => throw new FileNotFoundException(s"resource '$resource' was not found in the classpath from the given classloader.")
    }
}
