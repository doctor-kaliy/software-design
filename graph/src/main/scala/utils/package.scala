
package object utils {

    def getCenters(n: Int, width: Double, height: Double): Seq[Point] = {
        val globalRadius = math.min(width, height) / 3.0
        val center = Point(width / 2.0, height / 2.0)

        for {
            i <- (0 until n)
        } yield {
            val angle = 2.0 * math.Pi * i / n
            center + (Point(math.cos(angle), math.sin(angle)) * globalRadius)
        }
    }

}
