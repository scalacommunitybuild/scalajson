package scala.json.ast

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportAll}

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

  /**
    * Converts a [[JValue]] to a Javascript object/value that can be used within
    * Javascript
    *
    * @return
    */

  def toJsAny: js.Any
}

@JSExportAll
case object JNull extends JValue {
  def toUnsafe: unsafe.JValue = unsafe.JNull

  def toJsAny: js.Any = null
}

@JSExportAll
case class JString(value: String) extends JValue {
  def toUnsafe: unsafe.JValue = unsafe.JString(value)

  def toJsAny: js.Any = value
}

/**
  * If you are passing in a NaN or Infinity as a Double, JNumber will
  * return a JNull
  */

object JNumber {
  def apply(value: Int): JNumber = JNumber(BigDecimal(value))

  def apply(value: Integer): JNumber = JNumber(BigDecimal(value))

  def apply(value: Short): JNumber = JNumber(BigDecimal(value))

  def apply(value: Long): JNumber = JNumber(BigDecimal(value))

  def apply(value: BigInt): JNumber = JNumber(BigDecimal(value))

  /**
    * @param value
    * @return Will return a JNull if value is a Nan or Infinity
    */

  @JSExportAll
  def apply(value: Double): JValue = value match {
    case n if n.isNaN => JNull
    case n if n.isInfinity => JNull
    case _ => JNumber(BigDecimal(value))
  }

  def apply(value: Float): JNumber = JNumber(BigDecimal(value.toDouble)) // In Scala.js, float has the same representation as double
}

@JSExportAll
case class JNumber(value: BigDecimal) extends JValue {
  def to[B](implicit bigDecimalConverter: JNumberConverter[B]) = bigDecimalConverter(value)

  /**
    * Javascript specification for numbers specify a `Double`, so this is the default export method to `Javascript`
    *
    * @param value
    */
  def this(value: Double) = {
    this(BigDecimal(value))
  }

  /**
    * String constructor, so its possible to construct a [[JNumber]] with a larger precision
    * than the one defined by the IEEE 754. Note that when using it in Scala.js, it is possible for this to throw an
    * exception at runtime if you don't put in a correct number format for a [[scala.math.BigDecimal]].
    *
    * @param value
    */
  def this(value: String) = {
    this(BigDecimal(value))
  }

  def toUnsafe: unsafe.JValue = unsafe.JNumber(value)

  def toJsAny: js.Any = value.toDouble
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

@JSExport
case object JTrue extends JBoolean {
  def get = true

  @JSExport
  def toUnsafe: unsafe.JValue = unsafe.JTrue
}

@JSExport
case object JFalse extends JBoolean {
  def get = false

  @JSExport
  def toUnsafe: unsafe.JValue = unsafe.JFalse
}

case class JObject(value: Map[String, JValue] = Map.empty) extends JValue {

  /**
    * Construct a JObject using Javascript's object type, i.e. {} or new Object
    *
    * @param value
    */
  @JSExport def this(value: js.Dictionary[JValue]) = {
    this(value.toMap)
  }

  def toUnsafe: unsafe.JValue = {
    if (value.isEmpty) {
      unsafe.JObject(js.Array[unsafe.JField]())
    } else {
      val iterator = value.iterator
      val array = js.Array[unsafe.JField]()
      while (iterator.hasNext) {
        val (k, v) = iterator.next()
        array.push(unsafe.JField(k, v.toUnsafe))
      }
      unsafe.JObject(array)
    }
  }

  def toJsAny: js.Any = {
    if (value.isEmpty) {
      js.Dictionary[js.Any]().empty
    } else {
      val iterator = value.iterator
      val dict = js.Dictionary[js.Any]()
      while (iterator.hasNext) {
        val (k, v) = iterator.next()
        dict(k) = v.toJsAny
      }
      dict
    }
  }
}

object JArray {
  def apply(value: JValue, values: JValue*): JArray = JArray(value +: values.to[Vector])
}

case class JArray(value: Vector[JValue] = Vector.empty) extends JValue {
  /**
    * Construct a JArray using Javascript's array type, i.e. [] or new Array
    *
    * @param value
    */
  @JSExport def this(value: js.Array[JValue]) = {
    this(value.to[Vector])
  }

  def toUnsafe: unsafe.JValue = {
    if (value.isEmpty) {
      unsafe.JArray(js.Array[unsafe.JValue]())
    } else {
      val iterator = value.iterator
      val array = js.Array[unsafe.JValue]()
      while (iterator.hasNext) {
        array.push(iterator.next().toUnsafe)
      }
      unsafe.JArray(array)
    }
  }

  def toJsAny: js.Any = {
    if (value.isEmpty) {
      js.Array[js.Any]()
    } else {
      val iterator = value.iterator
      val array = js.Array[js.Any]()
      while (iterator.hasNext) {
        array.push(iterator.next().toJsAny)
      }
      array
    }
  }
}
