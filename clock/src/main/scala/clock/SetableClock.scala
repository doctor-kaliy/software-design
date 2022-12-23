package clock

import java.time.Instant

final case class SetableClock(private var instant: Instant) extends Clock {
    override def now = instant

    def set(newTime: Instant) {
        this.instant = newTime
    }
}
