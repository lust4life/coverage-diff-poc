package poc.jacoco

import org.jacoco.core.analysis.ICounter

object Implicits extends Implicits

trait Implicits {

  implicit class CounterWrapper(counter: ICounter) {
    def isCovered = {
      counter.getCoveredCount > 0
    }
  }

}
