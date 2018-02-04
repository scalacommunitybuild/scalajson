package specs.unsafe

import specs.Spec

class JNull extends Spec {
  "The unsafe.JNull value" should {
    "convert toStandard" in {
      scalajson.ast.unsafe.JNull.toStandard == scalajson.ast.JNull
    }

    "equals" in {
      scalajson.ast.unsafe.JNull == scalajson.ast.unsafe.JNull
    }
  }

}
