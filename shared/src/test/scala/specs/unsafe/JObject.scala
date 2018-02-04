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
  }

}
