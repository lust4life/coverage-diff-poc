package poc.example

import java.net.Socket

import poc.Implicits._
import poc.jacoco.JacocoClient

object utils {

  val port = 20201

  val jacocoClient = new JacocoClient(
    new Socket("localhost", port)
  )

  val mockServiceUrl = fromResource("mockservice.jar")

  def startJacocoAgent() = {
    val mockServiceFilePath = mockServiceUrl.getFile
    val jacocoagentJar = fromResource("jacocoagent.jar").getFile
    val sub =
      os.proc(
        "java",
        s"-javaagent:$jacocoagentJar=address=*,port=$port,output=tcpserver",
        "-jar", mockServiceFilePath).spawn()
    sub
  }

}
