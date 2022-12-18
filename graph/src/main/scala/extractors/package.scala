import scala.util.Try

package object extractors {
    object Int {
        def unapply(number: String): Option[Int] = Try(number.toInt).toOption
    }
}
