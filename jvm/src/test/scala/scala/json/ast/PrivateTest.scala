package scala.json.ast

import benchmark.Generators
import org.scalameter.Bench
import specs.Spec

/**
  * Created by matthewdedetrich on 5/04/2016.
  */
class PrivateTest extends Spec {
  def is =
    s2"""

  """

}


object PrivateBenchmark extends Bench.ForkedTime {

  performance of "privateMethods" in {
    measure method "hashcode" in {
      using(Generators.jNumber) in {
        jNumber: JNumber => jNumber.##
      }
    }

    measure method "equalsItself" in {
      using(Generators.jNumber) in {
        jNumber: JNumber => jNumber.equals(jNumber)
      }
    }
  }

}
