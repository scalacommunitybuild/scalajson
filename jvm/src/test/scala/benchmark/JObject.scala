package benchmark

import org.scalameter.Bench

object JObject extends Bench.ForkedTime {

  performance of "JObject" in {
    measure method "toFast" in {
      using(Generators.jObject) in {
        jObject => jObject.toUnsafe
      }
    }
  }
  
}
