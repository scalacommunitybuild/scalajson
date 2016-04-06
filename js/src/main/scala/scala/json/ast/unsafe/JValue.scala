package scala.json.ast.unsafe

import scala.json.ast
import scala.json.ast._
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll

sealed abstract class JValue extends Serializable with Product {
  /**
    * Converts a [[unsafe.JValue]] to a [[ast.JValue]]. Note that
    * when converting [[unsafe.JNumber]], this can throw runtime error if the underlying
    * string representation is not a correct number. Also when converting a [[ast.JObject]]
    * to a [[ast.JObject]], its possible to lose data if you have duplicate keys.
    *
    * @see https://www.ietf.org/rfc/rfc4627.txt
    * @return
    */

  def toStandard: ast.JValue

  /**
    * Converts a [[unsafe.JValue]] to a Javascript object/value that can be used within
    * Javascript
    *
    * @return
    */
  def toJsAny: js.Any
}

@JSExportAll
case object JNull extends JValue {
  def toStandard: ast.JValue = ast.JNull

  def toJsAny: js.Any = null
}

@JSExportAll
case class JString(value: String) extends JValue {
  def toStandard: ast.JValue = ast.JString(value)

  def toJsAny: js.Any = value
}

/**
  * If you are passing in a NaN or Infinity as a Double, JNumber
  * will contain "NaN" or "Infinity" as a String which means it will cause
  * issues for users when they use the value at runtime. You need to
  * check values yourself when constructing [[unsafe.JValue]]
  * to prevent this. This isn't checked by default for performance reasons.
  */

object JNumber {
  def apply(value: Int): JNumber = JNumber(value.toString)

  def apply(value: Byte): JNumber = JNumber(value.toString)

  def apply(value: Short): JNumber = JNumber(value.toString)

  def apply(value: Long): JNumber = JNumber(value.toString)

  def apply(value: BigInt): JNumber = JNumber(value.toString)

  def apply(value: BigDecimal): JNumber = JNumber(value.toString)

  def apply(value: Float): JNumber = JNumber(value.toString)

  def apply(value: Double): JNumber = JNumber(value.toString)

  def apply(value: Integer): JNumber = JNumber(value.toString)
}

// JNumber is internally represented as a string, to improve performance
@JSExportAll
case class JNumber(value: String) extends JValue {
  def to[B](implicit jNumberConverter: JNumberConverter[B]) = jNumberConverter(value)

  def toStandard: ast.JValue = ast.JNumber(value)

  @JSExportAll def this(value: Double) = {
    this(value.toString)
  }

  def toJsAny: js.Any = value.toInt
}

// Implements named extractors so we can avoid boxing
sealed abstract class JBoolean extends JValue {
  def get: Boolean

  def toJsAny: js.Any = get
}

object JBoolean {
  def apply(x: Boolean): JBoolean = if (x) JTrue else JFalse

  def unapply(x: JBoolean): Some[Boolean] = Some(x.get)
}

@JSExportAll
case object JTrue extends JBoolean {
  def get = true

  def toStandard: ast.JValue = ast.JTrue
}

@JSExportAll
case object JFalse extends JBoolean {
  def get = false

  def toStandard: ast.JValue = ast.JTrue
}

@JSExportAll
case class JField(field: String, value: JValue)

// JObject is internally represented as a mutable Array, to improve sequential performance
@JSExportAll
case class JObject(value: js.Array[JField] = js.Array()) extends JValue {
  @JSExportAll def this(value: js.Dictionary[JValue]) = {
    this({
      val array: js.Array[JField] = new js.Array()
      for (key <- value.keys) {
        array.push(JField(key, value(key)))
      }
      array
    })
  }

  def toStandard: ast.JValue = {
    // Javascript array.length across all major browsers has near constant cost, so we
    // use this to build the array http://jsperf.com/length-comparisons
    val length = value.length

    if (length == 0) {
      ast.JObject(Map.newBuilder[String, ast.JValue].result())
    } else {
      val b = Map.newBuilder[String, ast.JValue].result()
      var index = 0
      while (index < length) {
        val v = value(index)
        b + ((v.field, v.value.toStandard))
        index = index + 1
      }
      ast.JObject(b)
    }
  }

  def toJsAny: js.Any = {
    val length = value.length

    if (length == 0) {
      js.Dictionary[js.Any]().empty
    } else {
      val dict = js.Dictionary[js.Any]()

      var index = 0
      while (index < length) {
        val v = value(index)
        dict(v.field) = v.value.toJsAny
        index = index + 1
      }
      dict
    }
  }
}

// JArray is internally represented as a mutable js.Array, to improve sequential performance
@JSExportAll
case class JArray(value: js.Array[JValue] = js.Array()) extends JValue {
  def toStandard: ast.JValue = {
    // Javascript array.length across all major browsers has near constant cost, so we
    // use this to build the array http://jsperf.com/length-comparisons
    val length = value.length
    if (length == 0) {
      ast.JArray(Vector.newBuilder[ast.JValue].result())
    } else {
      val b = Vector.newBuilder[ast.JValue]
      var index = 0
      while (index < length) {
        b += value(index).toStandard
        index = index + 1
      }
      ast.JArray(b.result())
    }
  }

  def toJsAny: js.Any = value
}
