package clock

import java.time.Instant

trait Clock {
  def now: Instant
}
