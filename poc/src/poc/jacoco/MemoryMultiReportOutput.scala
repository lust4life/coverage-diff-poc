package poc.jacoco

import java.io.{ByteArrayOutputStream, OutputStream}

import org.jacoco.report.IMultiReportOutput

class MemoryMultiReportOutput extends IMultiReportOutput {
  val files = collection.mutable.Map[String, ByteArrayOutputStream]()

  override def createFile(path: String): OutputStream = {
    val out = new ByteArrayOutputStream()
    files += (path -> out)
    out
  }

  override def close(): Unit = {

  }
}
