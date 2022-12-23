package statistics

import clock.Clock
import java.util.ArrayDeque
import java.time.Instant
import scala.collection.mutable.{Map => MMap}
import java.time.Duration

final case class HourlyEventsStatistic(
    clock: Clock
) extends  EventsStatistic {
    import HourlyEventsStatistic._

    private val statistics: MMap[String, ArrayDeque[Instant]] = MMap.apply()

    override def incEvent(name: String): Unit = 
        statistics.getOrElseUpdate(name, new ArrayDeque()).addLast(clock.now)

    override def getEventStatisticByName(name: String): Double = {
        val now = clock.now
        val statistic = statistics.getOrElseUpdate(name, new ArrayDeque())
        getStatisticImpl(now)(statistic)
    }

    override def getAllEventStatistic: Map[String, Double] = {
        statistics.keySet.map(name => (name, getEventStatisticByName(name))).toMap
    }

    override def printStatistic(): Unit = {
        for {
            (name, rpm) <- getAllEventStatistic
        } {
            println(f"$name%s: $rpm%.4f")
        }
    }

}

object HourlyEventsStatistic {
    private val TIME_RANGE = 60.0 

    private def ensureStatistic(statistic: ArrayDeque[Instant], now: Instant) = {
        while (!statistic.isEmpty() && !statistic.peekFirst().isAfter(now.minus(Duration.ofHours(1)))) {
            statistic.pollFirst()
        }
    }

    private def getStatisticImpl(now: Instant)(statistic: ArrayDeque[Instant]): Double = {
        ensureStatistic(statistic, now)
        statistic.size() / TIME_RANGE
    }
}
