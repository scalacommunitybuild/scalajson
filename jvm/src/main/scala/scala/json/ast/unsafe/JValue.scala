package scala.json.ast.unsafe

import scala.json.ast
import scala.json.ast._

sealed abstract class JValue extends Serializable with Product {
  
  /**
    * Converts a [[unsafe.JValue]] to a [[ast.JValue]]. Note that
    * when converting [[unsafe.JNumber]], this can throw runtime error if the underlying
    * string representation is not a correct number. Also when converting a [[unsafe.JObject]]
    * to a [[ast.JObject]], its possible to lose data if you have duplicate keys.
    *
    * @see https://www.ietf.org/rfc/rfc4627.txt
    * @return
    */
  
  def toStandard: ast.JValue
}

case object JNull extends JValue {
  def toStandard: ast.JValue = ast.JNull
}

case class JString(value: String) extends JValue {
  def toStandard: ast.JValue = ast.JString(value)
}

/**
  * If you are passing in a NaN or Infinity as a Double, JNumber
  * will contain "NaN" or "Infinity" as a String which means it will cause
  * issues for users when they use the value at runtime. You need to
  * check values yourself when constructing [[scala.json.ast.unsafe.JValue]]
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

// JNumber is internally represented as a string, to improve performance
case class JNumber(value: String) extends JValue {
  def to[B](implicit jNumberConverter: JNumberConverter[B]) = jNumberConverter(value)

  def toStandard: ast.JValue = ast.JNumber(BigDecimal(value))
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

  def toStandard: ast.JValue = ast.JTrue
}

case object JFalse extends JBoolean {
  def get = false

  def toStandard: ast.JValue = ast.JFalse
}

case class JField(field: String, value: JValue)

// JObject is internally represented as a mutable Array, to improve sequential performance
case class JObject(value: Array[JField] = Array.empty) extends JValue {
  def toStandard: ast.JValue = {
    val length = value.length
    if (length == 0) {
      ast.JObject(Map[String, ast.JValue]())
    } else {
      var index = 0
      val b = Map.newBuilder[String, ast.JValue]
      while (index < length) {
        val v = value(index)
        b += ((v.field, v.value.toStandard))
        index = index + 1
      }
      ast.JObject(b.result())
    }
  }
}

// JArray is internally represented as a mutable Array, to improve sequential performance
case class JArray(value: Array[JValue] = Array.empty) extends JValue {
  def toStandard: ast.JValue = {
    val length = value.length
    if (length == 0) {
      ast.JArray(Vector[ast.JValue]())
    } else {
      var index = 0
      val b = Vector.newBuilder[ast.JValue]
      while (index < length) {
        b += value(index).toStandard
        index = index + 1
      }
      ast.JArray(b.result())
    }
  }
}
