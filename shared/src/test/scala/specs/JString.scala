package specs

import scalajson.ast.JString

class JString extends Spec {
  "The JString value" should {
    "read a String" in {
      forAll { s: String =>
        JString(s).value should be(s)
      }
    }

    "convert toUnsafe" in {
      forAll { s: String =>
        scalajson.ast.JString(s).toUnsafe == scalajson.ast.unsafe.JString(s)
      }
    }

    "equals" in {
      forAll { s: String =>
        scalajson.ast.JString(s) == scalajson.ast.JString(s)
      }
    }
  }
}
