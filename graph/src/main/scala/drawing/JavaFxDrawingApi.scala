package drawing

package drawing

import utils.Point
import javafx.scene.canvas.Canvas
import javafx.application.Application
import javafx.stage.Stage
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.scene.canvas.GraphicsContext
import scala.collection.mutable.ArrayBuffer

case class JavaFxDrawingApi(
    drawingAreaWidth: Long,
    drawingAreaHeight: Long
) extends DrawingApi {
    LaunchJfxApp.canvas = new Canvas(drawingAreaWidth, drawingAreaHeight)

    private val tasks: ArrayBuffer[GraphicsContext => Unit] = ArrayBuffer()

    override def drawCircle(
        center: Point,
        radius: Double
    ): Unit = {
        tasks += (_.fillOval(center.x - radius, center.y - radius, 2 * radius, 2 * radius))
    }
    
    override def drawLine(
        from: Point,
        to: Point
    ): Unit = tasks += (_.strokeLine(from.x, from.y, to.x, to.y))

    override def drawText(text: String, point: Point): Unit =
        tasks += (_.strokeText(text, point.x, point.y))

    override def show: Unit = {
        val graphicsContext = LaunchJfxApp.canvas.getGraphicsContext2D()
        graphicsContext.setStroke(Color.BLUE)
        tasks.foreach(_(graphicsContext))
        Application.launch(classOf[LaunchJfxApp])
    }
}