package specs.safe

import specs.Spec
import scala.json.ast.safe._

class JString extends Spec {
  def is =
    s2"""
  The JString value should
    read a String $readStringJString
  """

  def readStringJString = prop { s: String =>
    JString(s).value must beEqualTo(s)
  }
}
