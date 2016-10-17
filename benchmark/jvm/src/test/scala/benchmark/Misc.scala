package benchmark

import org.scalameter.{Bench, Gen}

/**
  * Misc benchmarks that are used to determine performance of various functions
  */
object Misc extends Bench.ForkedTime {

  val doubleAsString = Gen
    .range("seed")(300000, 1500000, 300000)
    .map(x => s"${x.toString}.${x.toString}")

  performance of "numeric operations" in {
    measure method "toDouble" in {
      using(doubleAsString) in { string =>
        string.toDouble
      }
    }

    measure method "doubleHashCode" in {
      using(doubleAsString) in { value =>
        {
          val asDouble = value.toDouble
          val long = java.lang.Double.doubleToLongBits(asDouble)
          (long ^ (long >>> 32)).toInt
        }
      }
    }

    measure method "manualHashcode" in {
      using(doubleAsString) in { value =>
        {
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
                  char =
                    value(i) // Fencepost, possible that this can be last character
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
                  char =
                    value(i) // Fencepost, possible that this can be last character
              }

              if (i < length) {
                result = 31 * result +
                '.': Int // The decimal is not finishing with a 0
              }
            } else {
              result = 31 * result + char: Int
            }

            i += 1
          }
          result
        }
      }
    }
  }
}
