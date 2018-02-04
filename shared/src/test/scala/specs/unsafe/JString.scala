package specs.unsafe

import specs.Spec
import scalajson.ast.unsafe._

class JString extends Spec {

  "The unsafe.String value" should {
    "read a String" in {
      forAll { s: String =>
        JString(s).value should be(s)
      }
    }

    "convert toStandard" in {
      forAll { s: String =>
        JString(s).toStandard should be(scalajson.ast.JString(s))
      }
    }
  }
}
