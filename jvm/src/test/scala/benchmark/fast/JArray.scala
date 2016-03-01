package benchmark.fast

import org.scalameter.Bench

object JArray extends Bench.ForkedTime {

  performance of "JArray" in {
    measure method "toSafe" in {
      using(Generators.jArray) in {
        jArray => jArray.toSafe
      }
    }
  }
  
}
