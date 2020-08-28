package poc.example.test

import org.jacoco.core.data.{ExecutionDataReader, ExecutionDataStore, SessionInfoStore}
import poc.Implicits._

object utils {
  def getMockCoverageExecData() = {
    val in = fromResource("aService.coverage.exec").openStream()
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
