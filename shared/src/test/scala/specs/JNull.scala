package specs

class JNull extends Spec {
  "The JNull value" should {
    "convert toUnsafe" in {
      scalajson.ast.JNull.toUnsafe == scalajson.ast.unsafe.JNull
    }

    "equals" in {
      scalajson.ast.JNull == scalajson.ast.JNull
    }
  }

}
