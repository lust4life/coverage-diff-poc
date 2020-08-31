package poc.jacoco

import java.net.Socket

import os.SubProcess
import utest._
import poc.Implicits._

object JacocoClientTest extends TestSuite {
  val port = 20203

  val tests = Tests {
    val client = new JacocoClient(
      new Socket("localhost", port)
    )

    "grab data only" - {
      val sub = startJacocoAgent()
      sub.stdout.readLine() ==> "run which case 1/2/3 ?"
      sub.stdin.writeLine("1")
      sub.stdin.flush()
      sub.stdout.readLine() ==> "10"
      sub.stdout.readLine() ==> "run which case 1/2/3 ?"

      val (session, data) = client.grab()
      session.getInfos.size() ==> 1
      data.getContents.size() ==> 2

      val (session1, data1) = client.grab()
      session1.getInfos.size() ==> 1
      // same with before
      data1.getContents.size() ==> 2

      closeSub(sub)
    }

    "grab data and reset" - {
      val sub = startJacocoAgent()
      sub.stdout.readLine() ==> "run which case 1/2/3 ?"
      sub.stdin.writeLine("2")
      sub.stdin.flush()
      sub.stdout.readLine() ==> "2"
      sub.stdout.readLine() ==> "run which case 1/2/3 ?"

      val (session, data) = client.grabAndReset()
      session.getInfos.size() ==> 1
      data.getContents.size() ==> 3

      val (session1, data1) = client.grab()
      session1.getInfos.size() ==> 1
      // since we have reset, so should have empty data
      data1.getContents.size() ==> 0

      closeSub(sub)
    }

    "grab after reset should be empty" - {
      val sub = startJacocoAgent()
      sub.stdout.readLine() ==> "run which case 1/2/3 ?"
      sub.stdin.writeLine("3")
      sub.stdin.flush()
      sub.stdout.readLine() ==> "do nothing"
      sub.stdout.readLine() ==> "run which case 1/2/3 ?"

      val (session, data) = client.grab()
      session.getInfos.size() ==> 1
      data.getContents.size() ==> 2

      client.reset()

      val (session1, data1) = client.grab()
      session1.getInfos.size() ==> 1
      // since we have reset the session
      data1.getContents.size() ==> 0

      closeSub(sub)
    }

  }

  def closeSub(sub: SubProcess): Unit = {
    sub.stdin.writeLine("over")
    sub.stdin.flush()
    sub.close()
  }

  def startJacocoAgent() = {
    val mockServiceFilePath = fromResource("mockservice.jar").getFile
    val jacocoagentJar = fromResource("jacocoagent.jar").getFile
    val sub =
      os.proc(
        "java",
        s"-javaagent:$jacocoagentJar=address=*,port=$port,output=tcpserver",
        "-jar", mockServiceFilePath).spawn()
    sub
  }

}
