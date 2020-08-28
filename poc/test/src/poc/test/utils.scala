package poc.test

import poc.Implicits._
import org.jacoco.core.data.{ExecutionDataReader, ExecutionDataStore, SessionInfoStore}

object utils {
  def getMockCoverageExecData() = {
    val in = fromResource("coverage.exec").openStream()
    val reader = new ExecutionDataReader(in)

    val sessionStore = new SessionInfoStore()
    val execDataStore = new ExecutionDataStore()
    reader.setExecutionDataVisitor(execDataStore)
    reader.setSessionInfoVisitor(sessionStore)

    reader.read()
    in.close()

    (sessionStore, execDataStore)
  }
}
