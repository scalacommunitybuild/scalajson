package specs.unsafe

import specs.Spec
import scalajson.ast.unsafe._
import Generators._

class JObject extends Spec {
  def is =
    s2"""
  The unsafe.JArray value should
    convert toStandard $toStandard
    have a useful toString ${_toString}
  """

  def toStandard = prop { jObject: scalajson.ast.unsafe.JObject =>
    val values = jObject.value.map { x =>
      (x.field, x.value.toStandard)
    }.toMap
    jObject.toStandard == scalajson.ast.JObject(values)
  }

  def _toString =
    "" + JObject(JField("a", JObject(JField("b", JFalse)))) ===
      "JObject([JField(a,JObject([JField(b,JFalse)]))])"

}
