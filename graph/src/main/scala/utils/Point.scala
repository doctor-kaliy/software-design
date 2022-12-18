package utils

final case class Point(x: Double, y: Double) {
    def +(other: Point) = Point(x + other.x, y + other.y)

    def *(scalar: Double) = Point(x * scalar, y * scalar)
}
