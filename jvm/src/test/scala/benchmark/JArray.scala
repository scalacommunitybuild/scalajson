package benchmark

import org.scalameter.Bench

object JArray extends Bench.ForkedTime {

  performance of "JArray" in {
    measure method "toFast" in {
      using(Generators.jArray) in {
        jArray => jArray.toUnsafe
      }
    }
  }

}
