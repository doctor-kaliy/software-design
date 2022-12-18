package extractors

sealed trait DrawingApiType

case object JavaFx extends DrawingApiType
case object Awt extends DrawingApiType

object DrawingApiType {
    def unapply(string: String): Option[DrawingApiType] = 
        string match {
            case "jfx" => Some(JavaFx)
            case "awt" => Some(Awt)
            case _ => None
        }    
}
