package extractors

sealed trait GraphKind

case object MatrixGraphKind extends GraphKind

case object ListGraphKind extends GraphKind

object GraphKind {
    def unapply(kind: String): Option[GraphKind] =
        kind match {
            case "list" => Some(ListGraphKind)
            case "matrix" => Some(MatrixGraphKind)
            case _ => None
        }
}
