package benchmark.unsafe

import org.scalameter.Bench

object JNumber extends Bench.ForkedTime {

  performance of "JNumber" in {
    measure method "toSafe" in {
      using(Generators.jNumber) in { jNumber =>
        jNumber.toStandard
      }
    }
  }
}
