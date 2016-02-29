package scala.json.ast.safe

abstract class JNumberConverter[T] {
  def apply(b: BigDecimal): T
}

object JNumberConverter {
  implicit val JNumberToInt = new JNumberConverter[Int] {
    @inline def apply(b: BigDecimal): Int = b.intValue()
  }

  implicit val JNumberToInteger = new JNumberConverter[Integer] {
    @inline def apply(b: BigDecimal): Integer = new Integer(b.intValue())
  }

  implicit val JNumberToShort = new JNumberConverter[Short] {
    @inline def apply(b: BigDecimal): Short = b.shortValue()
  }

  implicit val JNumberToLong = new JNumberConverter[Long] {
    @inline def apply(b: BigDecimal): Long = b.longValue()
  }

  implicit val JNumberToBigInt = new JNumberConverter[BigInt] {
    @inline def apply(b: BigDecimal): BigInt = b.toBigInt()
  }

  implicit val JNumberToFloat = new JNumberConverter[Float] {
    @inline def apply(b: BigDecimal): Float = b.floatValue()
  }

  implicit val JNumberToDouble = new JNumberConverter[Double] {
    @inline def apply(b: BigDecimal): Double = b.doubleValue()
  }

  implicit val JNumberToBigDecimal = new JNumberConverter[BigDecimal] {
    @inline def apply(b: BigDecimal): BigDecimal = b
  }

  implicit val JNumberString2CharArray = new JNumberConverter[Array[Char]] {
    @inline def apply(b: BigDecimal): Array[Char] = b.toString.toCharArray
  }

}
