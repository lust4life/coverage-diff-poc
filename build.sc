import mill._
import mill.api.Loose
import mill.define.Target
import scalalib._

object Deps {
  def scalaReflect(scalaVersion: String) = ivy"org.scala-lang:scala-reflect:${scalaVersion}"
}


object poc extends ScalaModule {
  override def scalaVersion = "2.13.3"


  override def ivyDeps: Target[Loose.Agg[Dep]] = Agg(
    ivy"com.github.javaparser:javaparser-core:3.16.1",
    ivy"com.lihaoyi::upickle:1.1.0",
    ivy"org.scala-lang.modules::scala-async:0.10.0",
    Deps.scalaReflect(scalaVersion()),
  )

  trait utest extends Tests {
    def testFrameworks = Seq("utest.runner.Framework")

    override def ivyDeps = Agg(
      ivy"com.lihaoyi::utest::0.7.4",
      ivy"org.mockito::mockito-scala:1.14.8",
    )
  }

  object test extends utest
  object integration extends utest
}

