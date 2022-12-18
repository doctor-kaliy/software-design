import scala.util.Try
import graphs.Graph
import drawing.DrawingApi
import graphs.ListGraph
import drawing.drawing.JavaFxDrawingApi
import graphs.Edge
import graphs.MatrixGraph
import drawing.AwtDrawingApi

object Main {
  val WIDTH: Long = 600
  val HEIGHT: Long = 600

  import extractors._

  private def constructGraph(
    kind: GraphKind,
    n: Int,
    fileName: String,
    drawingApi: DrawingApi
  ): Try[Graph] = Try {
    val source = scala.io.Source.fromFile(fileName)
    val lines = source.getLines().toList

    kind match {
      case ListGraphKind => 
        val (line :: _) = lines
        val numbers = line.split(" ").map(_.toInt)
        val (from, to) = numbers.zipWithIndex.partition(_._2 % 2 == 0)
        val edges = from.map(_._1)
          .zip(to.map(_._1))
          .toList
          .map {
            case (from, to) => Edge(from, to)
          }

        ListGraph(n, edges, drawingApi)

      case MatrixGraphKind => 
        val matrix = lines.map(line =>
          line.split(" ").toList map {
            case "0" => false
            case "1" => true
          }
        )

        MatrixGraph(n, matrix, drawingApi)
    }
  }

  def main(args: Array[String]): Unit =  args.toList match {
    case DrawingApiType(Awt) :: GraphKind(kind) :: Int(n) :: source :: Nil => 
      constructGraph(kind, n, source, AwtDrawingApi(WIDTH, HEIGHT))
        .map(_.drawGraph)
        .recover(e => println(e.getMessage()))
        .get

    case DrawingApiType(JavaFx) :: GraphKind(kind) :: Int(n) :: source :: Nil =>
      constructGraph(kind, n, source, JavaFxDrawingApi(WIDTH, HEIGHT))
        .map(_.drawGraph)
        .recover(e => println(e.getMessage()))
        .get

    case _ => 
      println("Invalid arguments. Expected <jfx or awt> <list or matrix> <number of nodes> <source file>")
  }
}