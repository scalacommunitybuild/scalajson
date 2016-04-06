package scala.json.ast

abstract class JNumberConverter[T] {
  def apply(string: String): T
}

object JNumberConverter {
  implicit val JNumberToInt = new JNumberConverter[Int] {
    @inline def apply(string: String): Int = string.toInt
  }

  implicit val JNumberToInteger = new JNumberConverter[Integer] {
    @inline def apply(string: String): Integer = new Integer(string.toInt)
  }

  implicit val JNumberToShort = new JNumberConverter[Short] {
    @inline def apply(string: String): Short = string.toShort
  }

  implicit val JNumberToLong = new JNumberConverter[Long] {
    @inline def apply(string: String): Long = string.toLong
  }

  implicit val JNumberToBigInt = new JNumberConverter[BigInt] {
    @inline def apply(string: String): BigInt = BigInt(string)
  }

  implicit val JNumberToFloat = new JNumberConverter[Float] {
    @inline def apply(string: String): Float = string.toFloat
  }

  implicit val JNumberToDouble = new JNumberConverter[Double] {
    @inline def apply(string: String): Double = string.toDouble
  }

  implicit val JNumberToBigDecimal = new JNumberConverter[BigDecimal] {
    @inline def apply(string: String): BigDecimal = BigDecimal(string)
  }

  implicit val JNumberString2CharArray = new JNumberConverter[Array[Char]] {
    @inline def apply(string: String): Array[Char] = string.toCharArray
  }

}
