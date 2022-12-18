package drawing

import utils.Point

trait DrawingApi {
    val drawingAreaWidth: Long
    
    val drawingAreaHeight: Long
    
    def drawCircle(
        center: Point,
        radius: Double
    ): Unit
    
    def drawLine(
        from: Point,
        to: Point
    ): Unit

    def drawText(text: String, point: Point): Unit

    def show: Unit
}
