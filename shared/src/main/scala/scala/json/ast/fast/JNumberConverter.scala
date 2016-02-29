package scala.json.ast.fast

abstract class JNumberConverter[T] {
  def apply(s: String): T
}

object JNumberConverter {
  implicit val JNumberString2BigDecimal = new JNumberConverter[BigDecimal] {
    @inline def apply(s: String): BigDecimal = BigDecimal(s)
  }

  implicit val JNumberString2Long = new JNumberConverter[Long] {
    @inline def apply(s: String): Long = s.toLong
  }

  implicit val JNumberString2Int = new JNumberConverter[Int] {
    @inline def apply(s: String): Int = s.toInt
  }

  implicit val JNumberString2Integer = new JNumberConverter[Integer] {
    @inline def apply(s: String): Integer = new Integer(s)
  }

  implicit val JNumberString2Double = new JNumberConverter[Double] {
    @inline def apply(s: String): Double = s.toDouble
  }

  implicit val JNumberString2Float = new JNumberConverter[Float] {
    @inline def apply(s: String): Float = s.toFloat
  }

  implicit val JNumberString2BigInt = new JNumberConverter[BigInt] {
    @inline def apply(s: String): BigInt = BigInt(s)
  }

  implicit val JNumberString2Short = new JNumberConverter[Short] {
    @inline def apply(s: String): Short = s.toShort
  }

  implicit val JNumberString2String = new JNumberConverter[String] {
    @inline def apply(s: String): String = s
  }

  implicit val JNumberString2CharArray = new JNumberConverter[Array[Char]] {
    @inline def apply(s: String): Array[Char] = s.toCharArray
  }
}
