package graphs

import drawing._

abstract class Graph(
    private[graphs] val drawingApi: DrawingApi
) {
    def drawGraph: Unit
}

object Graph {
    val NODE_RADIUS: Double = 30
}