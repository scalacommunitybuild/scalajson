package benchmark.unsafe

import org.scalameter.Bench

object JObject extends Bench.ForkedTime {

  performance of "JObject" in {
    measure method "toSafe" in {
      using(Generators.jObject) in {
        jObject => jObject.toStandard
      }
    }
  }

}
