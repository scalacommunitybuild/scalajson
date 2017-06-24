package specs

import Generators._

class JObject extends Spec {
  def is =
    s2"""
  The JArray value should
   convert toUnsafe $toUnsafe
   equals $testEquals
  """

  def toUnsafe = prop { jObject: scalajson.ast.JObject =>
    val values = jObject.value.map {
      case (k, v) =>
        scalajson.ast.unsafe.JField(k, v.toUnsafe)
    }
    Utils.unsafeJValueEquals(jObject.toUnsafe,
                             scalajson.ast.unsafe.JObject(values.toArray))
  }

  def testEquals = prop { jObject: scalajson.ast.JObject =>
    scalajson.ast.JObject(jObject.value) must beEqualTo(
      scalajson.ast.JObject(jObject.value))
  }
}
