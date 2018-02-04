package specs.unsafe

import specs.Spec
import scalajson.ast.unsafe._
import Generators._

class JObject extends Spec {

  "The unsafe.JObject value" should {
    "convert toStandard" in {
      forAll { jObject: scalajson.ast.unsafe.JObject =>
        val values = jObject.value.map { x =>
          (x.field, x.value.toStandard)
        }.toMap
        jObject.toStandard == scalajson.ast.JObject(values)
      }
    }

    "have a useful toString" in {
      "" + JObject(JField("a", JObject(JField("b", JFalse)))) ===
        "JObject([JField(a,JObject([JField(b,JFalse)]))])"
    }
  }

}
