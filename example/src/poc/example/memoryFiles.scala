package poc.example

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

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
      val path = ctx.remainingPathSegments.filter(s => s != "." && s != "..").mkString("/")
      val stream = memoryFileMap.get(path)
      val (data, statusCode) =
        if (stream.isDefined) {
          (new ByteArrayInputStream(stream.get.toByteArray): Response.Data, 200)
        } else {
          ("": Response.Data, 404)
        }
      Response(data, statusCode, headers, Nil)
    })
  }

  def wrapPathSegment(s: String): Seq[String] = Seq(s)
}
