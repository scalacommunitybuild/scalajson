package benchmark

import org.scalameter.{Bench, Gen}


/**
  * Misc benchmarks that are used to determine performance of various functions
  */

object Misc extends Bench.ForkedTime {

  val doubleAsString = Gen.range("seed")(300000, 1500000, 300000).map(x => s"${x.toString}.${x.toString}")

  performance of "numeric operations" in {
    measure method "toDouble" in {
      using(doubleAsString) in {
        string => string.toDouble
      }
    }

    measure method "doubleHashCode" in {
      using (doubleAsString) in {
        value => {
          val asDouble = value.toDouble
          val long = java.lang.Double.doubleToLongBits(asDouble)
          (long^ (long >>> 32)).toInt
        }
      }
    }


    measure method "manualHashcode" in {
      using (doubleAsString) in {
        value => {
          var result = 31
          val length = value.length
          var i = 0

          if (value(0) == '-') { // Found a negative, increment by one
            result = result * 31 + '-': Int
            i = 1
          }

          var char = value(i)

          // First lets skip all leading zeroes
          while (char == '0') {
            i += 1
            char = value(i)
          }

          // From now on, we can just traverse all the chars

          while (i < length) {
            char = value(i)
            // if char is e, lowercase it
            if ((char | 0x20) == 'e') {

              result = 31 * result + 'e': Int

              if (value(i + 1) == '-') { // Found a negative or positive, increment by one
                i += 1
                char = value(i)
                result = result * 31 + value(i): Int
              } else if (value(i + 1) == '+') {
                i += 1
                char = value(i)
              }

              i += 1
              char = value(i)

              // Same as before, need to skip all leading zeroes
              while (char == '0') {
                i += 1
                char = value(i)
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
