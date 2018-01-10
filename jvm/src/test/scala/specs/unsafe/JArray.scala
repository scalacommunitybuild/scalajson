package specs.unsafe

import specs.Spec
import scalajson.ast.unsafe._
import Generators._

class JArray extends Spec {
  def is =
    s2"""
  The unsafe.JArray value should
    convert toStandard $toStandard
    have a useful toString ${_toString}
  """

  def toStandard = prop { jArray: scalajson.ast.unsafe.JArray =>
    val values = jArray.value.map(_.toStandard).toVector
    jArray.toStandard == scalajson.ast.JArray(values)
  }

  def _toString =
    "" + JArray(JTrue, JArray(JFalse)) ===
      "JArray([JTrue, JArray([JFalse])])"

}
