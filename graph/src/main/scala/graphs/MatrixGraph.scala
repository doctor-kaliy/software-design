package graphs

import drawing._
import utils._

case class MatrixGraph(
    n: Int,
    matrix: List[List[Boolean]],
    drawing: DrawingApi
) extends Graph(drawing) {
    
    def drawGraph: Unit = {
        val centers = getCenters(n, drawingApi.drawingAreaWidth, drawingApi.drawingAreaHeight)
        
        for {
            (line, i) <- matrix.zipWithIndex
            (element, j) <- line.zipWithIndex
        } {
            if (element) {
                drawingApi.drawLine(centers(i), centers(j))
            }
        }

        centers.foreach(point => drawingApi.drawCircle(point, Graph.NODE_RADIUS))
        
        centers.zipWithIndex.foreach {
            case (point, index) => drawingApi.drawText(index.toString(), point)
        }

        drawingApi.show
    }
}

