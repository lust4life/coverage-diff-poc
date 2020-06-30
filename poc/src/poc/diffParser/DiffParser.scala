package poc.diffParser

import java.io.InputStream

import poc.domain.{Created, DiffFile, DiffGenerator}

class DiffParser extends DiffGenerator {
  /**
   * 解析 diff，生成 diff-result
   *
   * @return
   */
  override def parse(in: InputStream): Seq[DiffFile] = {
    val json = ujson.read(in)
    json.arr.map(x => {
      val file = x.obj
      val newName = file("newName").str
      val fileStatus = Created(newName)

      val changedLines =
        file("blocks").arr
          .flatMap(block => {
            val newNumbers = block("lines").arr.map(_.obj.get("newNumber").map(_.num))
            val newStartLine = block.obj.get("newStartLine").map(_.num)
            newNumbers.addOne(newStartLine)
          })
          .filter(_.isDefined)
          .map(_.get)
          .toSet

      DiffFile(fileStatus, changedLines)
    }).toSeq
  }
}