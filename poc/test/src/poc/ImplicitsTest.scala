package poc

import utest._

import scala.util.Try

object ImplicitsTest extends TestSuite {
  val tests = Tests {
    "cast should throw when failed" - {
      Try {
        Seq(1, 2).cast[String]
      }.failed.get.getMessage ==> "java.lang.Integer can't cast to java.lang.String"
    }

    "ofType should filter" - {
      Seq(1, 2, "4", "5").ofType[Int] ==> Seq(1, 2)
      Seq(1, 2, "4", "5").ofType[String] ==> Seq("4", "5")
    }

    "memorize should using cache" - {
      var count = 0
      val someFunc: Any => Int = memorize {
        case _ => {
          count += 1
          count
        }
      }
      someFunc(1) ==> 1
      someFunc(1) ==> 1
      someFunc(1) ==> 1
      someFunc(2) ==> 2
      someFunc(2) ==> 2
    }
  }
}
