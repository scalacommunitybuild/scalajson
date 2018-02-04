package specs.unsafe

import specs.Spec
import specs.EqualityImplicits._
import Generators._

class JsJValue extends Spec {
  "The Scala.js unsafe.JValue value" should {
    "convert to jsAny" in {
      forAll { jValue: scalajson.ast.unsafe.JValue =>
        jValue should equal(jValue.toJsAny)
      }
    }
  }
}
