package statistics

trait EventsStatistic {
  def incEvent(name: String): Unit
  def getEventStatisticByName(name: String): Double
  def getAllEventStatistic: Map[String, Double]
  def printStatistic(): Unit
}
