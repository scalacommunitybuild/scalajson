package specs.unsafe

import specs.Spec
import scala.json.ast.unsafe._
import Generators._

class JArray extends Spec {
  def is =
    s2"""
  The unsafe.JArray value should
    convert toStandard $toStandard
  """

  def toStandard = prop {jArray: scala.json.ast.unsafe.JArray =>
    val values = jArray.value.map(_.toStandard).to[Vector]
    jArray.toStandard == scala.json.ast.JArray(values)
  }
}
