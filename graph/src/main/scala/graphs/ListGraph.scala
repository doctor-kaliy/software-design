package graphs

import drawing._
import utils._

case class ListGraph(
    n: Int,
    edges: List[Edge],
    drawing: DrawingApi
) extends Graph(drawing) {
    def drawGraph: Unit = {
        val centers = getCenters(n, drawingApi.drawingAreaWidth, drawingApi.drawingAreaHeight)
        
        edges.foreach {
            case Edge(from, to) => 
                drawingApi.drawLine(centers(from), centers(to))
        }

        centers.foreach(point => drawingApi.drawCircle(point, Graph.NODE_RADIUS))
        
        centers.zipWithIndex.foreach {
            case (point, index) => drawingApi.drawText(index.toString(), point)
        }

        drawingApi.show
    }
}
