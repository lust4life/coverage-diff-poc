import mill._
import mill.api.Loose
import mill.define.Target
import scalalib._

object poc extends ScalaModule {
  override def scalaVersion = "2.13.3"

  override def ivyDeps: Target[Loose.Agg[Dep]] = Agg(
    ivy"com.github.javaparser:javaparser-core:3.16.1"
  )


  object test extends Tests {
    def testFrameworks = Seq("utest.runner.Framework")
    def ivyDeps = Agg(ivy"com.lihaoyi::utest::0.7.4")
  }
}

