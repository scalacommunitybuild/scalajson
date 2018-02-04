package specs

import Generators._
import EqualityImplicits._

class JsJValue extends Spec {
  "The Scala.js JValue value" should {
    "convert to jsAny" in {
      forAll { jValue: scalajson.ast.JValue =>
        jValue should equal(jValue.toJsAny)
      }
    }
  }
}
