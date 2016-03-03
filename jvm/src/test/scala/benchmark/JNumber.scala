package benchmark

import org.scalameter.Bench

object JNumber extends Bench.ForkedTime {

  performance of "JNumber" in {
    measure method "toFast" in {
      using(Generators.jNumber) in {
        jNumber => jNumber.toUnsafe
      }
    }
  }
  
}
