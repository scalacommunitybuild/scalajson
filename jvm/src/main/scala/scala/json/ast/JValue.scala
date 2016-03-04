package scala.json.ast

import scala.offheap.data

sealed abstract class JValue extends Product with Serializable {

  /**
    * Converts a [[JValue]] to a [[unsafe.JValue]]. Note that
    * when converting [[JObject]], this can produce [[unsafe.JObject]] of
    * unknown ordering, since ordering on a [[scala.collection.Map]] isn't defined. Duplicate keys will also
    * be removed in an undefined manner.
    *
    * @see https://www.ietf.org/rfc/rfc4627.txt
    * @return
    */

  def toUnsafe: unsafe.JValue
}

case object JNull extends JValue {
  def toUnsafe: unsafe.JValue = unsafe.JNull
}

case class JString(value: String) extends JValue {
  def toUnsafe: unsafe.JValue = unsafe.JString(value)
}

/**
  * If you are passing in a NaN or Infinity as a Double, JNumber will
  * return a JNull
  */

object JNumber {
  private[this] final val mc = BigDecimal.defaultMathContext

  def apply(value: Int): JNumber = JNumber(BigDecimal(value))

  def apply(value: Short): JNumber = JNumber(BigDecimal(value))

  def apply(value: Long): JNumber = JNumber(BigDecimal(value))

  def apply(value: BigInt): JNumber = JNumber(BigDecimal(value))

  def apply(value: Float): JNumber = JNumber({
    // BigDecimal.decimal doesn't exist on 2.10, so this is just the Scala 2.11 implementation
    new BigDecimal(new java.math.BigDecimal(java.lang.Float.toString(value), mc), mc)
  })

  /**
    * @param value
    * @return Will return a JNull if value is a Nan or Infinity
    */

  def apply(value: Double): JValue = value match {
    case n if n.isNaN => JNull
    case n if n.isInfinity => JNull
    case _ => JNumber(BigDecimal(value))
  }

  def apply(value: Integer): JNumber = JNumber(BigDecimal(value))

  def apply(value: Array[Char]): JNumber = JNumber(BigDecimal(value))
}

case class JNumber(value: BigDecimal) extends JValue {
  def to[B](implicit bigDecimalConverter: JNumberConverter[B]) = bigDecimalConverter(value)

  def toUnsafe: unsafe.JValue = unsafe.JNumber(value)
}

// Implements named extractors so we can avoid boxing
sealed abstract class JBoolean extends JValue {
  def get: Boolean
}

object JBoolean {
  def apply(x: Boolean): JBoolean = if (x) JTrue else JFalse

  def unapply(x: JBoolean): Some[Boolean] = Some(x.get)
}

case object JTrue extends JBoolean {
  def get = true

  def toUnsafe: unsafe.JValue = unsafe.JTrue
}

case object JFalse extends JBoolean {
  def get = false

  def toUnsafe: unsafe.JValue = unsafe.JFalse
}

case class JObject(value: Map[String, JValue] = Map.empty) extends JValue {
  // The minimum size to start using offheap due to overhead
  private[this] val sizeOffset = 12
  
  def toUnsafe: unsafe.JValue = {
    if (value.isEmpty) {
      unsafe.JArray(Array.ofDim[unsafe.JValue](0))
    } else {
      
      val length = value.size
      
      if (length < sizeOffset) {
        val array = Array.ofDim[unsafe.JField](value.size)
        var index = 0
        for ((k, v) <- value) {
          array(index) = unsafe.JField(k, v.toUnsafe)
          index = index + 1
        }
        unsafe.JObject(array)
      } else {
        import scala.offheap._
        import scala.offheap.internal.SunMisc._

        val sizeEstimate = 256

        val tempChunkSize = UNSAFE.pageSize() * length * sizeEstimate

        val chunkSize = if (UNSAFE.pageSize() > tempChunkSize) {
          sizeEstimate * UNSAFE.pageSize()
        } else {
          tempChunkSize
        }

        implicit val props = Region.Props(new Pool(
          alloc = malloc,
          pageSize = UNSAFE.pageSize(),
          chunkSize = chunkSize
        ))
        
        Region { implicit r =>
          val array = Array.empty[unsafe.JField]
          var index = 0
          for ((k, v) <- value) {
            array(index) = unsafe.JField(k, v.toUnsafe)
            index = index + 1
          }
          unsafe.JObject(array.toArray)
        }
      }
    }
  }
}

object JArray {
  def apply(value: JValue, values: JValue*): JArray = JArray(value +: values.to[Vector])
}

case class JArray(value: Vector[JValue] = Vector.empty) extends JValue {
  // The minimum size to start using offheap due to overhead
  private[this] val sizeOffset = 8
  
  def toUnsafe: unsafe.JValue = {
    val length = value.length
    if (length == 0) {
      unsafe.JArray(Array.ofDim[unsafe.JValue](0))
    } else {
      if (length < sizeOffset) {
        val array = Array.ofDim[unsafe.JValue](length)
        val iterator = value.iterator
        var index = 0
        while (iterator.hasNext) {
          array(index) = iterator.next().toUnsafe
          index = index + 1
        }
        unsafe.JArray(array)
      } else {
        import scala.offheap._
        import scala.offheap.internal.SunMisc._

        val sizeEstimate = 256

        val tempChunkSize = UNSAFE.pageSize() * length * sizeEstimate

        val chunkSize = if (UNSAFE.pageSize() > tempChunkSize) {
          sizeEstimate * UNSAFE.pageSize()
        } else {
          tempChunkSize
        }

        implicit val props = Region.Props(new Pool(
          alloc = malloc,
          pageSize = UNSAFE.pageSize(),
          chunkSize = chunkSize
        ))

        val array = Region { implicit r =>
          val array = Array.empty[unsafe.JValue]
          val iterator = value.iterator
          var index = 0
          while (iterator.hasNext) {
            array(index) = iterator.next().toUnsafe
            index = index + 1
          }
          array
        }
        unsafe.JArray(array.toArray)
      }
    }
  }
}
