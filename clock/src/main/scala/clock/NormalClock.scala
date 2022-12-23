package clock

import java.time.Instant

final case class NormalClock() extends Clock {
  override def now: Instant = Instant.now()

}
