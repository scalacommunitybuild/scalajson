package scala.collection.immutable

import scala.collection.{GenTraversableOnce, mutable}
import scala.collection.generic.{CanBuildFrom, ImmutableMapFactory}

/** This class implements immutable maps using a vector/map-based data structure, which preserves insertion order.
  *  Instances of `VectorMap` represent empty maps; they can be either created by
  *  calling the constructor directly, or by applying the function `VectorMap.empty`.
  *
  *  Unlike `ListMap`, `VectorMap` has amortized effectively constant lookup at the expense
  *  of using extra memory
  * @author Matthew de Detrich
  * @tparam A
  * @tparam B
  */
@SerialVersionUID(1858299024439116764L)
@deprecatedInheritance(
  "The semantics of immutable collections makes inheriting from VectorMap error-prone.")
class VectorMap[A, +B](private val fields: Vector[A],
                       private val underlying: Map[A, B])
    extends AbstractMap[A, B]
    with Map[A, B]
    with MapLike[A, B, VectorMap[A, B]]
    with Serializable {

  override def empty: VectorMap[A, Nothing] = VectorMap.empty

  override def +[B1 >: B](kv: (A, B1)): VectorMap[A, B1] = {
    if (underlying.contains(kv._1)) {
      new VectorMap(fields, underlying.updated(kv._1, kv._2))
    } else {
      new VectorMap(fields :+ kv._1, underlying.updated(kv._1, kv._2))
    }
  }

  override def ++[B1 >: B](xs: GenTraversableOnce[(A, B1)]): VectorMap[A, B1] = {
    val fieldsBuilder = Vector.newBuilder[A]
    xs.foreach { value =>
      if (!underlying.contains(value._1)) {
        fieldsBuilder += value._1
      }
    }
    new VectorMap(fields ++ fieldsBuilder.result(), underlying ++ xs)
  }

  override def get(key: A): Option[B] = underlying.get(key)

  override def getOrElse[B1 >: B](key: A, default: => B1): B1 =
    underlying.getOrElse(key, default)

  override def iterator: Iterator[(A, B)] = new Iterator[(A, B)] {
    private val fieldsIterator = fields.iterator

    override def hasNext: Boolean = fieldsIterator.hasNext

    override def next(): (A, B) = {
      val field = fieldsIterator.next()
      (field, underlying(field))
    }
  }

  def toMap: Map[A, B] = underlying

  override def -(key: A): VectorMap[A, B] =
    new VectorMap(fields.filterNot(_ == key), underlying - key)

  override def keys: scala.Iterable[A] = fields.iterator.toIterable

  override def values: scala.Iterable[B] = new Iterable[B] {
    override def iterator: Iterator[B] = {
      new Iterator[B] {
        private val fieldsIterator = fields.iterator

        override def hasNext: Boolean = fieldsIterator.hasNext

        override def next(): B = {
          val field = fieldsIterator.next()
          underlying(field)
        }
      }
    }
  }

  override def isEmpty: Boolean = fields.isEmpty

  override def size: Int = fields.size

  override def apply(k: A): B = underlying(k)

  override def updated[B1 >: B](key: A, value: B1): VectorMap[A, B1] = {
    if (underlying.contains(key)) {
      new VectorMap(fields, underlying.updated(key, value))
    } else {
      new VectorMap(fields :+ key, underlying.updated(key, value))
    }
  }
}

object VectorMap extends ImmutableMapFactory[VectorMap] {
  implicit def canBuildFrom[A, B]
    : CanBuildFrom[Coll, (A, B), VectorMap[A, B]] =
    new MapCanBuildFrom[A, B]

  override def empty[A, B]: VectorMap[A, B] =
    new VectorMap(Vector.empty, Map.empty)

  override def newBuilder[A, B]: mutable.Builder[(A, B), VectorMap[A, B]] =
    new VectorMapBuilder
}

class VectorMapBuilder[A, B] extends mutable.Builder[(A, B), VectorMap[A, B]] {
  private val fieldBuilder = Vector.newBuilder[A]
  private val underlyingBuilder = Map.newBuilder[A, B]

  override def +=(elem: (A, B)): VectorMapBuilder.this.type = {
    underlyingBuilder += elem
    fieldBuilder += elem._1
    this
  }

  override def clear(): Unit = {
    underlyingBuilder.clear()
    fieldBuilder.clear()
  }

  override def result(): VectorMap[A, B] = {
    new VectorMap(fieldBuilder.result(), underlyingBuilder.result())
  }
}
