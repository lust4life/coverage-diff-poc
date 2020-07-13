package poc.diff

import java.io.InputStream

trait DiffStreamLocator {

  /**
   * get diff stream from two source code version
   * @param oldVersion old source code version
   * @param newVersion new source code version
   * @return
   */
  def getDiffStream(oldVersion: String, newVersion: String): InputStream
}
