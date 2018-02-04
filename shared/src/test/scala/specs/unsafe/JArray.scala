package specs.unsafe

import specs.Spec
import scalajson.ast.unsafe._
import Generators._

class JArray extends Spec {
  "The unsafe.JArray value" should {
    "convert toStandard" in {
      forAll { jArray: scalajson.ast.unsafe.JArray =>
        val values = jArray.value.map(_.toStandard).toVector
        jArray.toStandard == scalajson.ast.JArray(values)
      }
    }
  }
}
