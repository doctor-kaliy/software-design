import clock.SetableClock
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.time.Instant
import scala.concurrent.duration.DurationInt
import statistics.HourlyEventsStatistic
import statistics.EventsStatistic
import java.time.Duration

class HourlyEventStatisticsSpec extends AnyFlatSpec with Matchers {

  def withStats(f: (SetableClock, EventsStatistic) => Any): Any = {
    val clock: SetableClock = SetableClock(Instant.now)
    val stats: EventsStatistic = HourlyEventsStatistic(clock)

    f(clock, stats)
  }

  it should "be 0 for nonexistent event" in withStats { (clock, stats) =>
    stats.getEventStatisticByName("not_here") should be (0.0)
  }

  it should "count rpm" in withStats { (clock, stats) =>
    (1 to 100).foreach { _ => stats.incEvent("event") }

    stats.getEventStatisticByName("event") should be (100 / 60.0)
  }

  it should "only consider events within 1 hour" in withStats { (clock, stats) =>
    (1 to 5).foreach {_ => stats.incEvent("event") }
    
    clock.set(clock.now.plus(Duration.ofHours(1)))
    
    (1 to 7).foreach { _ => stats.incEvent("event") }

    stats.getEventStatisticByName("event") should be (7.0 / 60)
  }

  it should "count rpms within the time range" in withStats { (clock, stats) =>
    (1 to 28).foreach { _ => stats.incEvent("0") }

    clock.set(clock.now.plus(Duration.ofMinutes(20)))

    (1 to 50).foreach { _ => stats.incEvent("0") }
    (1 to 10).foreach { _ => stats.incEvent("1") }

    clock.set(clock.now.plus(Duration.ofMinutes(20)))

    (1 to 9).foreach { _ => stats.incEvent("2") }
    (1 to 30).foreach { _ => stats.incEvent("1") }
    (1 to 15).foreach { _ => stats.incEvent("0") }

    clock.set(clock.now.plus(Duration.ofMinutes(20)))

    (1 to 5).foreach { _ => stats.incEvent("0") }

    stats.getAllEventStatistic shouldBe Map(
      "0" -> 70.0 / 60,
      "1" -> 40.0 / 60,
      "2" -> 9.0 / 60,
    )
  }
}