package poc.diff
package jsonGenerator

import java.io.InputStream

import ujson.Value

import scala.collection.mutable

class DiffJsonParser extends DiffGenerator {
  /**
   * 解析 diff，生成 diff-result
   *
   * @return
   */
  override def generate(in: InputStream): Seq[DiffFile] = {
    val json = ujson.read(in)
    json.arr.map(x => {
      val file = x.obj
      val newName = file("newName").str
      val oldName = file("oldName").str
      val language = file("language").str

      val isDeleted = file.get("isDeleted").map(_.boolOpt).flatten
      isDeleted match {
        case Some(true) => Deleted(newName, language)
        case _ => {
          val isNew = file.get("isNew").map(_.boolOpt).flatten
          isNew match {
            case Some(true) => Created(newName, language)
            case _ => {
              val changedLines = parseChangedLines(file)
              val isRename = file.get("isRename").map(_.boolOpt).flatten
              isRename match {
                case Some(true) => Rename(oldName, newName, language, changedLines)
                case _ => Changed(newName, language, changedLines)
              }
            }
          }
        }
      }
    }).toSeq
  }

  private def parseChangedLines(file: mutable.LinkedHashMap[String, Value]) = {
    file("blocks").arr
      .flatMap(block => {
        val newNumbers =
          block("lines").arr
            .filter(_.obj.get("type").exists(_.str != "context"))
            .map(_.obj.get("newNumber").map(_.num))
        val newStartLine = block.obj.get("newStartLine").map(_.num)
        newNumbers.addOne(newStartLine)
      })
      .flatten
      .toSet
  }
}