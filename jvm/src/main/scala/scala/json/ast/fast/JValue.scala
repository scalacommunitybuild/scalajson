package scala.json.ast.fast

import scala.json.ast.safe

sealed abstract class JValue extends Serializable with Product {

  /**
    * Converts a [[scala.json.ast.fast.JValue]] to a [[scala.json.ast.safe.JValue]]. Note that
    * when converting [[scala.json.ast.fast.JNumber]], this can throw runtime error if the underlying
    * string representation is not a correct number
    *
    * @see https://www.ietf.org/rfc/rfc4627.txt
    * @return
    */
  def toSafe: safe.JValue
}

case object JNull extends JValue {
  def toSafe: safe.JValue = safe.JNull
}

case class JString(value: String) extends JValue {
  def toSafe: safe.JValue = safe.JString(value)
}

/**
  * If you are passing in a NaN or Infinity as a Double, JNumber
  * will contain "NaN" or "Infinity" as a String which means it will cause
  * issues for users when they use the value at runtime. You need to
  * check values yourself when constructing [[scala.json.ast.fast.JValue]]
  * to prevent this. This isn't checked by default for performance reasons.
  */

object JNumber {
  def apply(value: Int): JNumber = JNumber(value.toInt.toString)

  def apply(value: Integer): JNumber = JNumber(value.toString)

  def apply(value: Short): JNumber = JNumber(value.toString)

  def apply(value: Long): JNumber = JNumber(value.toString)

  def apply(value: BigInt): JNumber = JNumber(value.toString)

  def apply(value: BigDecimal): JNumber = JNumber(value.toString)

  def apply(value: Float): JNumber = JNumber(value.toString)

  def apply(value: Double): JNumber = JNumber(value.toString)

  def apply(value: Array[Char]): JNumber = JNumber(value.mkString)
}

/**
  * JNumber is internally represented as a string, to improve performance
  *
  * @param value
  */
case class JNumber(value: String) extends JValue {
  def to[B](implicit jNumberConverter: JNumberConverter[B]) = jNumberConverter(value)

  def toSafe: safe.JValue = safe.JNumber(BigDecimal(value))
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

  def toSafe: safe.JValue = safe.JTrue
}

case object JFalse extends JBoolean {
  def get = false

  def toSafe: safe.JValue = safe.JFalse
}

case class JField(field: String, value: JValue)

/**
  * JObject is internally represented as a mutable Array, to improve sequential performance
  *
  * @param value
  */
case class JObject(value: Array[JField] = Array.empty) extends JValue {
  def toSafe: safe.JValue = {
    val length = value.length
    if (length == 0) {
      safe.JObject(Map[String, safe.JValue]())
    } else {
      var index = 0
      val b = Map.newBuilder[String, safe.JValue]
      while (index < length) {
        val v = value(index)
        b += ((v.field, v.value.toSafe))
        index = index + 1
      }
      safe.JObject(b.result())
    }
  }
}

/**
  * JArray is internally represented as a mutable Array, to improve sequential performance
  *
  * @param value
  */
case class JArray(value: Array[JValue] = Array.empty) extends JValue {
  def toSafe: safe.JValue = {
    val length = value.length
    if (length == 0) {
      safe.JArray(Vector[safe.JValue]())
    } else {
      var index = 0
      val b = Vector.newBuilder[safe.JValue]
      while (index < length) {
        b += value(index).toSafe
        index = index + 1
      }
      safe.JArray(b.result())
    }
  }
}
