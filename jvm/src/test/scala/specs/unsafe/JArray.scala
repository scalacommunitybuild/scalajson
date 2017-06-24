package specs.unsafe

import specs.Spec
import scalajson.ast.unsafe._
import Generators._

class JArray extends Spec {
  def is =
    s2"""
  The unsafe.JArray value should
    convert toStandard $toStandard
  """

  def toStandard = prop { jArray: scalajson.ast.unsafe.JArray =>
    val values = jArray.value.map(_.toStandard).to[Vector]
    jArray.toStandard == scalajson.ast.JArray(values)
  }
}
