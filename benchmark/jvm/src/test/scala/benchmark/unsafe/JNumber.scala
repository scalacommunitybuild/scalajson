package benchmark.unsafe

import benchmark.Constants
import org.scalameter.Bench

object JNumber extends Bench.ForkedTime {

  performance of "JNumber" in {
    measure method "toSafe" in {
      using(Generators.jNumber) in { jNumber =>
        jNumber.toStandard
      }
    }
    measure method "toInt" in {
      using(Generators.jNumber) in { jNumber =>
        var i = 0
        while (i < Constants.innerLoopConstant) {
          jNumber.toInt
          i += 1
        }
      }
    }
    measure method "toLong" in {
      using(Generators.jNumber) in { jNumber =>
        var i = 0
        while (i < Constants.innerLoopConstant) {
          jNumber.toLong
          i += 1
        }
      }
    }
    measure method "toBigInt" in {
      using(Generators.jNumber) in { jNumber =>
        var i = 0
        while (i < Constants.innerLoopConstant) {
          jNumber.toBigInt
          i += 1
        }
      }
    }
    measure method "toBigDecimal" in {
      using(Generators.jNumber) in { jNumber =>
        var i = 0
        while (i < Constants.innerLoopConstant) {
          jNumber.toBigDecimal
          i += 1
        }
      }
    }
    measure method "toDouble" in {
      using(Generators.jNumber) in { jNumber =>
        var i = 0
        while (i < Constants.innerLoopConstant) {
          jNumber.toDouble
          i += 1
        }
      }
    }
  }
}
