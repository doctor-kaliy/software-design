object LRUCache {

  private case class OptEqOps[A <: AnyRef](self: Option[A]) {
    def refEq(other: Option[A]): Boolean =
      self.isEmpty && other.isEmpty ||
        (self.get eq other.get)
  }

  implicit private def toOptEqOps[A <: AnyRef]: Option[A] => OptEqOps[A] = OptEqOps.apply

  private case class Node[K, V](var prev: Option[Node[K, V]], key: K, var value: V, var next: Option[Node[K, V]]) {
    override def toString(): String = (key, value).toString()
  }

  private type Cache[K, V] = scala.collection.mutable.Map[K, Node[K, V]]

  private object CacheExtractor {
    def unapply[K, V](
        cache: Cache[K, V]
    ): Option[List[(K, V)]] =
      Some(
        cache.toList
          .map { case (_, node) => (node.key, node.value) }
      )
  }

  private object NodeListExtractor {
    def unapply[K, V](
        headTail: (Option[Node[K, V]], Option[Node[K, V]])
    ): Option[List[(K, V)]] =
      headTail match {
        case (None, tail) => Some(Nil)
        case (Some(Node(_, key, value, next)), tail) =>
          unapply((next, tail))
            .map((key, value) :: _)
      }

  }

  private def getKeyValueList[K, V](cache: Cache[K, V]): List[(K, V)] =
    cache.toList.map { case (_, Node(_, key, value, _)) => (key, value) }

}

final class LRUCache[K, V](capacity: Int) {
  import LRUCache._

  private val cache: Cache[K, V]           = scala.collection.mutable.Map()
  private var listHead: Option[Node[K, V]] = None
  private var listTail: Option[Node[K, V]] = None

  assert(invariants)

  private def invariants: Boolean = {
    val NodeListExtractor(nodeList) = (listHead, listTail)
    val CacheExtractor(cacheList)   = cache

    capacity > 0 &&
    capacity >= nodeList.size &&
    nodeList.toSet == cacheList.toSet &&
    nodeList.toSet.size == nodeList.size
  }

  private def removeNode(node: Node[K, V]): Unit = {
    val NodeListExtractor(listBeforeRemove) = (listHead, listTail)
    assert {
      (listBeforeRemove contains (node.key, node.value)) &&
      (listBeforeRemove.size <= 1 && (listHead refEq listTail) ||
        listBeforeRemove.size > 1 && !(listHead refEq listTail))
    }

    node match {
      case _ if cache.size == 1 =>
        listHead = None
        listTail = None
      case Node(_, key, value, next) if listHead.get.key == key =>
        listHead = next
        next.foreach(_.prev = None)

      case Node(prev, key, value, _) if listTail.get.key == key =>
        listTail = prev
        prev.foreach(_.next = None)

      case Node(prev, _, _, next) =>
        prev.foreach(_.next = next)
        next.foreach(_.prev = prev)
    }

    assert {
      val NodeListExtractor(listAfterRemove) = (listHead, listTail)

      !(listAfterRemove contains (node.key, node.value)) &&
      (listAfterRemove.size <= 1 && (listHead refEq listTail) ||
        listAfterRemove.size > 1 && !(listHead refEq listTail)) &&
      listAfterRemove.size + 1 == listBeforeRemove.size
    }
  }

  private def insertNodeFirst(node: Node[K, V]): Node[K, V] = {
    node.next = listHead
    node.prev = None

    assert {
      val NodeListExtractor(listBeforeInsert) = (listHead, listTail)

      !(listBeforeInsert contains (node.key, node.value)) &&
      (listBeforeInsert.size <= 1 && (listHead refEq listTail) ||
        listBeforeInsert.size > 1 && !(listHead refEq listTail))
    }

    listTail = Some(listTail.fold(node)(identity))
    listHead.foreach(_.prev = Some(node))
    listHead = Some(node)

    assert {
      val NodeListExtractor(listAfterInsert) = (listHead, listTail)

      (listAfterInsert contains (node.key, node.value)) &&
      (listAfterInsert.size <= 1 && (listHead refEq listTail) ||
        listAfterInsert.size > 1 && !(listHead refEq listTail)) &&
      listHead.nonEmpty &&
      listHead.get.key == node.key && listHead.get.value == node.value
    }

    node
  }

  def get(key: K): Option[V] = {
    val result = cache
      .get(key)
      .map { node =>
        removeNode(node)
        insertNodeFirst(node)
        node.value
      }

    assert {
      val NodeListExtractor(nodeList) = (listHead, listTail)

      invariants &&
      (!cache.contains(key) ||
        result.nonEmpty &&
        result.get == listHead.get.value &&
        listHead.get.key == key)
    }

    result
  }

  def put(key: K, value: V): Unit = {
    cache.updateWith(key) {
      case None =>
        val node = Node(None, key, value, None)
        Some(
          listTail.fold(insertNodeFirst(node)) { tail =>
            if (cache.size == capacity) {
              removeNode(tail)
              cache.remove(tail.key)
            }
            insertNodeFirst(node)
          }
        )

      case Some(oldNode) =>
        oldNode.value = value
        Some(oldNode)
    }

    assert {
      val NodeListExtractor(nodeList) = (listHead, listTail)

      invariants &&
      nodeList.contains((key, value)) &&
      listHead.nonEmpty &&
      cache(key).value == listHead.get.value &&
      listHead.get.key == key
    }
  }
}
