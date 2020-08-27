package poc.example

import java.io.ByteArrayOutputStream

import cask.endpoints.QueryParamReader
import cask.model.{Request, Response}
import cask.router.HttpEndpoint

class memoryFiles(val path: String,
                  headers: Seq[(String, String)] = Nil)
  extends HttpEndpoint[Map[String, ByteArrayOutputStream], Seq[String]] {

  val methods = Seq("get")
  type InputParser[T] = QueryParamReader[T]

  override def subpath = true

  def wrapFunction(ctx: Request, delegate: Delegate) = {
    delegate(Map()).map(memoryFileMap => {
      val path = ctx.remainingPathSegments.filter(s => s!="." && s!= "..").mkString("/")
      val relPath = java.nio.file.Paths.get(path)
      val stream = memoryFileMap.get(path)
      val (data: Response.Data, statusCode) =
        if (stream.isDefined) {
          (stream.get.toByteArray, 200)
        } else {
          ("", 404)
        }

      Response(data, statusCode, headers, Nil)
    })
  }

  def wrapPathSegment(s: String): Seq[String] = Seq(s)
}
