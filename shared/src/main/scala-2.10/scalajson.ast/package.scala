package scalajson

import java.math.MathContext

import scala.util.matching.Regex

package object ast {

  /**
    * A regex that will match any valid JSON number for unlimited
    * precision
    */
  protected[ast] val jNumberRegex: Regex =
    """-?(?:0|[1-9]\d*)(?:\.\d+)?(?:[eE][+-]?\d+)?""".r

  /**
    * Finds the hashcode for a numeric JSON string.
    *
    * @author Matthew de Detrich
    * @param value
    * @return
    */
  private[ast] def numericStringHashcode(value: String): Int = {
    var result = 31
    val length = value.length
    var i = 0

    if (value(0) == '-') {
      // Found a negative, increment by one
      result = result * 31 + '-': Int
      i = 1
    }

    var char = value(i)

    // From now on, we can just traverse all the chars

    var negativeFlag = false

    while (i < length) {
      char = value(i)
      // if char is e, lowercase it
      if ((char | 0x20) == 'e') {

        if (value(i + 1) == '-') {
          // Found a negative, increment by one
          i += 1
          char = value(i)
          negativeFlag = true
        } else if (value(i + 1) == '+') {
          // Found a positive, ignore
          i += 1
          char = value(i)
        }

        i += 1
        char = value(i)

        // Need to skip all leading zeroes, possible with e
        while (char == '0' && i < length) {
          i += 1
          if (i != length)
            char = value(i) // Fencepost, possible that this can be last character
        }

        if (i < length) {
          if (negativeFlag) {
            result = result * 31 + '-': Int
          }

          result = 31 * result + 'e': Int
        }
      } else if (char == '.') {

        i += 1
        char = value(i)

        while (char == '0' && i < length) {
          i += 1
          if (i != length)
            char = value(i) // Fencepost, possible that this can be last character
        }

        if (i < length) {
          result = 31 * result + '.': Int // The decimal is not finishing with a 0
        }
      } else {
        result = 31 * result + char: Int
      }

      i += 1
    }
    result
  }

  /** Tests two non-empty strings for equality, assuming both are decimal representations of numbers.
    *
    * Note: if the two strings are NOT decimal representations of numbers, the results of this method are undefined.
    * (It is likely but not guaranteed that the method will return `false` even if the two strings are identical.)
    *
    * Many thanks for @Ichoran (https://github.com/Ichoran) for the implementation

    * @author Rex Kerr
    * @see https://github.com/Ichoran/kse/blob/master/src/main/scala/jsonal/Jast.scala#L683-L917
    */
  private[ast] def numericStringEquals(a: String, b: String): Boolean = {
    var i = 0 // Index for a
    var j = 0 // Index for b
    if (a.length < 1 || b.length < 1) return false
    if (a.charAt(0) == '-') i += 1
    if (b.charAt(0) == '-') j += 1
    if (i >= a.length || j >= b.length) return false
    var ca = a.charAt(i) // Character at index of a
    var cb = b.charAt(j) // Character at index of b
    if (i != j) {
      // Different signs.  They'd better both be zero
      return {
        if (ca == '0' && cb == '0') {
          while (i < a.length - 1 && (ca == '0' || ca == '.')) {
            i += 1; ca = a.charAt(i)
          }
          while (j < b.length - 1 && (cb == '0' || cb == '.')) {
            j += 1; cb = b.charAt(j)
          }
          ((i == a.length - 1 && ca == '0') || (ca | 0x20) == 'e') &&
          ((j == b.length - 1 && cb == '0') || (cb | 0x20) == 'e')
        } else false
      }
    }
    var pa = 0 // Decimal point position for a
    var pb = 0 // Decimal point position for b
    if (ca == '0') {
      pa = -1
      if (i < a.length - 1) {
        val x = a.charAt(i + 1)
        if ((x | 0x20) != 'e') {
          if (x != '.') return false
          else if (i < a.length - 2) {
            i += 2
            ca = a.charAt(i)
            while (ca == '0' && i < a.length - 1) {
              i += 1; ca = a.charAt(i); pa -= 1
            }
          }
        }
      }
    }
    if (cb == '0') {
      pb = -1
      if (j < b.length - 1) {
        val y = b.charAt(j + 1)
        if ((y | 0x20) != 'e') {
          if (y != '.') return false
          else if (j < b.length - 2) {
            j += 2
            cb = b.charAt(j)
            while (cb == '0' && j < b.length - 1) {
              j += 1; cb = b.charAt(j); pb -= 1
            }
          }
        }
      }
      // Might both be zero.  Check!  (Can ignore exponents when both values are zero.)
      if (ca < '1' || ca > '9') return cb < '1' || cb > '9'
    }
    var fa = pa < 0 // Found a's decimal point?
    var fb = pb < 0 // Found b's decimal point?
    while (ca == cb && (ca | 0x20) != 'e' && i < a.length - 1 &&
           j < b.length - 1) {
      i += 1
      ca = a.charAt(i)
      if (!fa) {
        pa += 1
        if (ca == '.') {
          fa = true
          if (i < a.length - 1) {
            i += 1
            ca = a.charAt(i)
          }
        }
      }
      j += 1
      cb = b.charAt(j)
      if (!fb) {
        pb += 1
        if (cb == '.') {
          fb = true
          if (j < b.length - 1) {
            j += 1
            cb = b.charAt(j)
          }
        }
      }
    }
    if (ca != cb && (ca | 0x20) != 'e' && (cb | 0x20) != 'e') return false // Digits simply disagree
    // Capture any trailing zeros
    if (!(i >= a.length - 1 && j >= b.length - 1)) {
      if (j >= b.length - 1 || (cb | 0x20) == 'e') {
        if (j >= b.length - 1) {
          if (!fb) pb += 1
          // Advance a off the end, make sure it's all zeros
          i += 1
          ca = a.charAt(i)
          if (!fa) {
            pa += 1
            if (ca == '.') {
              fa = true
              if (i < a.length - 1) {
                i += 1
                ca = a.charAt(i)
              }
            }
          }
        }
        while (i < a.length - 1 && ca == '0') {
          i += 1
          ca = a.charAt(i)
          if (!fa) {
            pa += 1
            if (ca == '.') {
              fa = true
              if (i < a.length - 1) {
                i += 1
                ca = a.charAt(i)
              }
            }
          }
        }
        if (ca >= '1' && ca <= '9') return false // Extra digits on a
      } else if (i >= a.length - 1 || (ca | 0x20) == 'e') {
        if (i >= a.length - 1) {
          if (!fa) pa += 1
          // Advance b off the end, make sure it's all zeros
          j += 1
          cb = b.charAt(j)
          if (!fb) {
            pb += 1
            if (cb == '.') {
              fb = true
              if (j < b.length - 1) {
                j += 1
                cb = b.charAt(j)
              }
            }
          }
        }
        while (j < b.length - 1 && cb == '0') {
          j += 1
          cb = b.charAt(j)
          if (!fb) {
            pb += 1
            if (cb == '.') {
              fb = true
              if (j < b.length - 1) {
                j += 1
                cb = b.charAt(j)
              }
            }
          }
        }
        if (cb >= '1' && cb <= '9') return false // Extra digits on b
      }
    }
    if (pa > 0) pa -= 1
    if (pb > 0) pb -= 1
    if ((ca | 0x20) == 'e' && (cb | 0x20) == 'e') {
      if (i >= a.length - 1) return false
      i += 1
      ca = a.charAt(i)
      val nega =
        if (ca == '-' || ca == '+') {
          val ans = ca == '-'
          if (i >= a.length - 1) return false
          i += 1
          ca = a.charAt(i)
          ans
        } else false
      if (j >= b.length - 1) return false
      j += 1
      cb = b.charAt(j)
      val negb =
        if (cb == '-' || cb == '+') {
          val ans = cb == '-'
          if (j >= b.length - 1) return false
          j += 1
          cb = b.charAt(j)
          ans
        } else false
      if (a.length - i < 10 && b.length - j < 10) {
        val ea = a.substring(i).toInt
        val eb = b.substring(j).toInt
        (if (negb) pb - eb else pb + eb) == (if (nega) pa - ea else pa + ea)
      } else if (a.length - i < 18 && b.length - j < 18) {
        val ea = a.substring(i).toLong
        val eb = b.substring(j).toLong
        (if (negb) pb - eb else pb + eb) == (if (nega) pa - ea else pa + ea)
      } else {
        val ea = BigInt(a.substring(i))
        val eb = BigInt(b.substring(j))
        (if (negb) pb - eb else pb + eb) == (if (nega) pa - ea else pa + ea)
      }
    } else if ((ca | 0x20) == 'e') {
      if (i >= a.length - 1) return false
      i += 1
      ca = a.charAt(i)
      val nega =
        if (ca == '-' || ca == '+') {
          val ans = ca == '-'
          if (i >= a.length - 1) return false
          i += 1
          ca = a.charAt(i)
          ans
        } else false
      if (a.length - i < 10) {
        val ea = a.substring(i).toInt
        pb == (if (nega) pa - ea else pa + ea)
      } else if (a.length - i < 18) {
        val ea = a.substring(i).toLong
        pb == (if (nega) pa - ea else pa + ea)
      } else {
        val ea = BigInt(a.substring(i))
        pb == (if (nega) pa - ea else pa + ea)
      }
    } else if ((cb | 0x20) == 'e') {
      if (j >= b.length - 1) return false
      j += 1
      cb = b.charAt(j)
      val negb =
        if (cb == '-' || cb == '+') {
          val ans = cb == '-'
          if (j >= b.length - 1) return false
          j += 1
          cb = b.charAt(j)
          ans
        } else false
      if (b.length - j < 10) {
        val eb = b.substring(j).toInt
        pa == (if (negb) pb - eb else pb + eb)
      } else if (b.length - j < 18) {
        val eb = b.substring(j).toLong
        pa == (if (negb) pb - eb else pb + eb)
      } else {
        val eb = BigInt(b.substring(j))
        pa == (if (negb) pb - eb else pb + eb)
      }
    } else pa == pb
  }

  @inline private[ast] def radix: Int = 10

  private[ast] def toInt(value: String): Option[Int] = {
    @inline def maxLengthConstant: Int = 10
    var limit: Int = -Integer.MAX_VALUE
    var decimalFlag = false
    var result: Int = 0
    var resultBigInt: BigInt = null
    var negative = false
    var multmin: Int = 0
    var char: Char = 0
    var i = 0
    var eFlag = false
    var trailingZeroes: Int = 0
    var negativeEFlag: Boolean = false
    var resultNegativeEFlag: Int = 0
    var digitLength: Int = 0
    multmin = limit / radix
    if (value.charAt(0) == '-') {
      limit = Integer.MIN_VALUE
      negative = true
      i += 1
    }

    while (i < value.length) {
      char = value.charAt(i)
      if (char == '.')
        decimalFlag = true
      else if ((char | 0x20) == 'e') {
        eFlag = true
        val charNext = value.charAt(i + 1)
        if (charNext == '-')
          negativeEFlag = true
        if (negativeEFlag || charNext == '+') {
          i += 1
        }
      } else {
        if (!(eFlag || decimalFlag))
          digitLength += 1

        val digit = Character.digit(char, radix)

        if (digit == 0)
          if (!decimalFlag)
            trailingZeroes += 1
          else if (!decimalFlag)
            trailingZeroes = 0

        if (digit != 0 && (decimalFlag || eFlag))
          if (!negativeEFlag)
            return None
          else {
            if (trailingZeroes != 0) {
              resultNegativeEFlag *= radix
              resultNegativeEFlag += digit
              if (trailingZeroes >= resultNegativeEFlag) {
                var i2: Int = 0
                while (i2 < resultNegativeEFlag) {
                  if (resultBigInt == null)
                    result = result / radix
                  else
                    resultBigInt = resultBigInt / radix
                  i2 += 1
                }
              } else
                return None
            }
          }

        val maxLenCheck = digitLength <= maxLengthConstant

        if (resultBigInt == null) {
          if (result < multmin && maxLenCheck && !eFlag)
            return None
        } else {
          if (resultBigInt < multmin && maxLenCheck && !eFlag)
            return None
        }

        if (!(digit == 0 && (decimalFlag || eFlag))) {

          if (!negativeEFlag) {
            if (digitLength == maxLengthConstant -1) {
              var result2: Int = result
              result2 *= radix

              if (!negative && result2 < 0 || negative && result2 > 0) {
                resultBigInt = BigInt(result)
              }
            }

            if (resultBigInt == null) {
              result *= radix
              if (result < limit + digit && maxLenCheck)
                return None
              result -= digit
            } else {
              resultBigInt *= radix
              if (resultBigInt < limit + digit && maxLenCheck)
                return None
              resultBigInt -= digit
            }
          }
        }
      }
      i += 1
    }
    if (resultBigInt == null) {
      if (result < limit)
        None
      else {
        if (negative)
          Some(result)
        else
          Some(-result)
      }
    } else {
      if (resultBigInt < limit)
        None
      else {
        if (negative)
          Some(resultBigInt.toInt)
        else
          Some(-resultBigInt.toInt)
      }
    }
  }

  private[ast] def toLong(value: String): Option[Long] = {
    @inline def maxLengthConstant: Int = 19
    var limit: Long = -Long.MaxValue
    var decimalFlag = false
    var result: Long = 0
    var resultBigInt: BigInt = null
    var negative = false
    var multmin: Long = 0
    var char: Char = 0
    var i = 0
    var eFlag = false
    var trailingZeroes: Int = 0
    var negativeEFlag: Boolean = false
    var resultNegativeEFlag: Long = 0
    var digitLength: Int = 0
    multmin = limit / radix
    if (value.charAt(0) == '-') {
      limit = Long.MinValue
      negative = true
      i += 1
    }

    while (i < value.length) {
      char = value.charAt(i)
      if (char == '.')
        decimalFlag = true
      else if ((char | 0x20) == 'e') {
        eFlag = true
        val charNext = value.charAt(i + 1)
        if (charNext == '-')
          negativeEFlag = true
        if (negativeEFlag || charNext == '+') {
          i += 1
        }
      } else {
        if (!(eFlag || decimalFlag))
          digitLength += 1

        val digit = Character.digit(char, radix)

        if (digit == 0)
          if (!decimalFlag)
            trailingZeroes += 1
          else if (!decimalFlag)
            trailingZeroes = 0

        if (digit != 0 && (decimalFlag || eFlag))
          if (!negativeEFlag)
            return None
          else {
            if (trailingZeroes != 0) {
              resultNegativeEFlag *= radix
              resultNegativeEFlag += digit
              if (trailingZeroes >= resultNegativeEFlag) {
                var i2: Int = 0
                while (i2 < resultNegativeEFlag) {
                  if (resultBigInt == null)
                    result = result / radix
                  else
                    resultBigInt = resultBigInt / radix
                  i2 += 1
                }
              } else
                return None
            }
          }

        val maxLenCheck = digitLength <= maxLengthConstant

        if (resultBigInt == null) {
          if (result < multmin && maxLenCheck && !eFlag)
            return None
        } else {
          if (resultBigInt < multmin && maxLenCheck && !eFlag)
            return None
        }

        if (!(digit == 0 && (decimalFlag || eFlag))) {

          if (!negativeEFlag) {
            if (digitLength == maxLengthConstant -1) {
              var result2: Long = result
              result2 *= radix

              if (!negative && result2 < 0 || negative && result2 > 0) {
                resultBigInt = BigInt(result)
              }
            }

            if (resultBigInt == null) {
              result *= radix
              if (result < limit + digit && maxLenCheck)
                return None
              result -= digit
            } else {
              resultBigInt *= radix
              if (resultBigInt < limit + digit && maxLenCheck)
                return None
              resultBigInt -= digit
            }
          }
        }
      }
      i += 1
    }
    if (resultBigInt == null) {
      if (result < limit)
        None
      else {
        if (negative)
          Some(result)
        else
          Some(-result)
      }
    } else {
      if (resultBigInt < limit)
        None
      else {
        if (negative)
          Some(resultBigInt.toLong)
        else
          Some(-resultBigInt.toLong)
      }
    }
  }

  private[ast] def toBigInt(value: String): Option[BigInt] = {
    var decimalFlag = false
    var result: Int = 0 // assume that values are initially small when we convert to bigInt
    var resultBigInt: BigInt = null
    var negative = false
    var char: Char = 0
    var i = 0
    var eFlag = false
    var trailingZeroes: Int = 0
    var negativeEFlag: Boolean = false
    var resultNegativeEFlag: Int = 0
    var digitLength: Int = 0
    if (value.charAt(0) == '-') {
      negative = true
      i += 1
    }

    while (i < value.length) {
      char = value.charAt(i)
      if (char == '.')
        decimalFlag = true
      else if ((char | 0x20) == 'e') {
        eFlag = true
        val charNext = value.charAt(i + 1)
        if (charNext == '-')
          negativeEFlag = true
        if (negativeEFlag || charNext == '+') {
          i += 1
        }
      } else {
        if (!(eFlag || decimalFlag))
          digitLength += 1

        val digit = Character.digit(char, radix)

        if (digit == 0)
          if (!decimalFlag)
            trailingZeroes += 1
          else if (!decimalFlag)
            trailingZeroes = 0

        if (digit != 0 && (decimalFlag || eFlag))
          if (!negativeEFlag)
            return None
          else {
            if (trailingZeroes != 0) {
              resultNegativeEFlag *= radix
              resultNegativeEFlag += digit
              if (trailingZeroes >= resultNegativeEFlag) {
                var i2: Int = 0
                while (i2 < resultNegativeEFlag) {
                  result = result / radix
                  i2 += 1
                }
              } else
                return None
            }
          }

        if (!(digit == 0 && (decimalFlag || eFlag))) {

          if (!negativeEFlag) {
            var result2: Int = result
            result2 *= radix
            if (!negative && result2 < 0 || negative && result2 > 0) {
              resultBigInt = BigInt(result)
            }

            if (resultBigInt == null) {
              result *= radix
              result -= digit
            } else {
              resultBigInt *= radix
              resultBigInt -= digit
            }
          }
        }
      }
      i += 1
    }

    if (resultBigInt == null) {
      if (negative)
        Some(BigInt(result))
      else
        Some(BigInt(-result))
    } else {
      if (negative)
        Some(resultBigInt)
      else
        Some(resultBigInt)
    }
  }

  private[ast] def toDouble(value: String): Option[Double] = {
    try {
      val asDouble = value.toDouble
      if (BigDecimal(value, MathContext.UNLIMITED) == BigDecimal(asDouble, MathContext.UNLIMITED))
        Some(asDouble)
      else
        None
    } catch {
      case _: java.lang.NumberFormatException => None
    }
  }

  private[ast] def toBigDecimal(value: String): Option[BigDecimal] = {
    try {
      Some(BigDecimal(value, MathContext.UNLIMITED))
    } catch {
      case _: java.lang.NumberFormatException => None
    }
  }
}
