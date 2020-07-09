package poc.jacoco

import java.net.Socket

import org.jacoco.core.data.{ExecutionDataReader, ExecutionDataStore, SessionInfoStore}
import org.jacoco.core.runtime.{IRemoteCommandVisitor, RemoteControlWriter}

import scala.util.Using

class JacocoClient(socket: => Socket) {

  def reset(): Unit = {
    Using.resource(socket) {
      socket => {
        val remoteControl = new RemoteControlWriter(socket.getOutputStream())
        remoteControl.visitDumpCommand(false, true)
      }
    }
  }

  def grab(): (SessionInfoStore, ExecutionDataStore) = grab(reset = false)

  def grabAndReset(): (SessionInfoStore, ExecutionDataStore) = grab(reset = true)

  protected def grab(reset: Boolean): (SessionInfoStore, ExecutionDataStore) = {
    grabInfo(remote => remote.visitDumpCommand(true, reset))
  }

  private def grabInfo(doWithRemote: IRemoteCommandVisitor => Unit): (SessionInfoStore, ExecutionDataStore) = {
    Using.resource(socket) {
      socket => {
        val remoteControl = new RemoteControlWriter(socket.getOutputStream())
        val reader = new ExecutionDataReader(socket.getInputStream())

        val sessionInfoStore = new SessionInfoStore()
        val execDataStore = new ExecutionDataStore()
        reader.setExecutionDataVisitor(execDataStore)
        reader.setSessionInfoVisitor(sessionInfoStore)

        doWithRemote(remoteControl)

        reader.read()
        (sessionInfoStore, execDataStore)
      }
    }
  }
}
