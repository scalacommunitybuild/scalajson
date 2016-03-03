package specs

import scala.json.ast.JString

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
