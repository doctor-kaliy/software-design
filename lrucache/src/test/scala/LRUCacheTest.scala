import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.language.implicitConversions

final class LRUCacheTest extends AnyFlatSpec with Matchers {

  private def testCache[K, V](
      capacity: Int
  )(
      test: LRUCache[K, V] => Any
  ): Any = test(new LRUCache(capacity))

  "Cache" should "return x after adding x" in
    testCache[Int, Int](
      capacity = 1
    ) { cache =>
      cache.put(1, 1)
      cache.get(1) shouldBe Some(1)
    }

  "Cache" should "fail with 0 capacity" in {
    assertThrows[AssertionError](new LRUCache[Int, Int](0))
  }

  "Cache" should "return None after getting by non existing key" in
    testCache[Int, Int](
      capacity = 10
    )(cache => cache.get(1) shouldBe None)

  "Cache" should "remove last recently used key" in
    testCache[Int, Int](
      capacity = 3
    ) { cache =>
      cache.put(1, 1)
      cache.put(2, 2)
      cache.put(3, 3)
      cache.put(4, 4)

      (cache.get(2), cache.get(3), cache.get(4), cache.get(1)) shouldBe (Some(2), Some(3), Some(4), None)
    }

  "Cache" should "replace last recently used key after getting existing key" in
    testCache[Int, Int](
      capacity = 3
    ) { cache =>
      cache.put(1, 1)
      cache.put(2, 2)
      cache.put(3, 3)
      cache.get(1)
      cache.put(4, 4)

      (cache.get(1), cache.get(3), cache.get(4), cache.get(2)) shouldBe (Some(1), Some(3), Some(4), None)
    }
}
