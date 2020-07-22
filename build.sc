import $ivy.`com.lihaoyi::mill-contrib-scoverage:$MILL_VERSION`

import mill._
import mill.api.Loose
import mill.define.Target
import poc.utest
import scalalib._
import mill.contrib.scoverage.ScoverageModule

object Deps {
  def scalaReflect(scalaVersion: String) = ivy"org.scala-lang:scala-reflect:${scalaVersion}"
}

object poc extends ScoverageModule {
  override def scalaVersion = "2.13.3"

  override def scoverageVersion= "1.4.1"

  override def ivyDeps: Target[Loose.Agg[Dep]] = Agg(
    ivy"com.github.javaparser:javaparser-core:3.16.1",
    ivy"org.jacoco:org.jacoco.core:0.8.5",
    ivy"org.jacoco:org.jacoco.report:0.8.5",
    ivy"com.lihaoyi::upickle:1.1.0",
    ivy"org.scala-lang.modules::scala-async:0.10.0",
    ivy"com.lihaoyi::os-lib:0.7.0",
    Deps.scalaReflect(scalaVersion()),
  )

  trait utest extends ScoverageTests {
    def testFrameworks = Seq("utest.runner.Framework")

    override def ivyDeps = Agg(
      ivy"com.lihaoyi::utest::0.7.4",
      ivy"org.mockito::mockito-scala:1.14.8",
    )
  }

  object test extends utest

  object integration extends utest


  object github extends ScoverageModule {
    override def moduleDeps: Seq[JavaModule] = Seq(poc)
    override def scoverageVersion= "1.4.1"
    override def scalaVersion = poc.scalaVersion

    override def ivyDeps: Target[Loose.Agg[Dep]] = Agg(
      ivy"com.lihaoyi::requests:0.6.2",
      ivy"org.scala-lang.modules::scala-async:0.10.0",
      Deps.scalaReflect(scalaVersion()),
    )

    object test extends utest {
      override def moduleDeps: Seq[JavaModule] = github +: super.moduleDeps
    }

  }

  object mongodb extends ScalaModule {
    override def scalaVersion = poc.scalaVersion

    override def ivyDeps: Target[Loose.Agg[Dep]] = Agg(
      ivy"com.lihaoyi::upickle:1.1.0",
      ivy"org.scala-lang.modules::scala-async:0.10.0",
      Deps.scalaReflect(scalaVersion()),
    )
  }

}

object example extends ScalaModule {
  override def scalaVersion = poc.scalaVersion

  override def moduleDeps: Seq[JavaModule] = Seq(poc, poc.github)

  override def ivyDeps: Target[Loose.Agg[Dep]] = Agg(
    ivy"com.lihaoyi::cask:0.6.7",
    ivy"com.lihaoyi::upickle:1.1.0",
    ivy"org.scala-lang.modules::scala-async:0.10.0",
    ivy"com.lihaoyi::os-lib:0.7.0",
    Deps.scalaReflect(scalaVersion()),
  )


  object test extends utest {
    override def moduleDeps: Seq[JavaModule] = example +: super.moduleDeps
  }

}

