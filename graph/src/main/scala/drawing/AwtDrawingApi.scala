package drawing

import utils.Point
import scala.collection.mutable.ArrayBuffer
import java.awt._
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.font.FontRenderContext
import java.awt.font.TextLayout
import java.awt.geom.Ellipse2D
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import java.util.function.Consumer

case class AwtDrawingApi(
    drawingAreaWidth: Long,
    drawingAreaHeight: Long, 
) extends DrawingApi {
    private val circles: ArrayBuffer[Shape] = ArrayBuffer()
    private val lines: ArrayBuffer[Shape] = ArrayBuffer()
    private val texts: ArrayBuffer[(String, Point)] = ArrayBuffer()

    override def drawCircle(center: Point, radius: Double): Unit = 
        circles += new Ellipse2D.Double(center.x - radius, center.y - radius, radius * 2, radius * 2)

    override def drawLine(from: Point, to: Point): Unit = 
        lines += new Line2D.Double(new Point2D.Double(from.x, from.y), new Point2D.Double(to.x, to.y))

    override def drawText(text: String, point: Point): Unit =
        texts += text -> point

    override def show: Unit = {
        LaunchAwtApp.paintImpl = 
        new Consumer[Graphics] {
            def accept(g: Graphics): Unit = {
                val graphics2D = g.asInstanceOf[Graphics2D]
                graphics2D.setColor(Color.BLACK)
                circles.foreach(graphics2D.fill(_))
                lines.foreach(graphics2D.draw(_))
                graphics2D.setColor(Color.WHITE)
                val font: Font = graphics2D.getFont().deriveFont(Font.BOLD, 20)
                val fontRenderContext = graphics2D.getFontRenderContext()
                texts.foreach {
                    case (text, point) =>
                        new TextLayout(text, font, fontRenderContext)
                            .draw(graphics2D, point.x.asInstanceOf[Float], point.y.asInstanceOf[Float])
                }
            }
        }

        val app = new LaunchAwtApp();

        app.addWindowListener(
            new WindowAdapter() {
                override def windowClosing(e: WindowEvent): Unit = {
                    System.exit(0)
                }
            }
        )

        app.setSize(drawingAreaWidth.asInstanceOf[Int], drawingAreaHeight.asInstanceOf[Int])
        app.setVisible(true)
    }
  
}
