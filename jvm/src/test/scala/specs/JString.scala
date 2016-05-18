package specs

import scala.json.ast.JString

class JString extends Spec {
  def is =
    s2"""
  The JString value should
    read a String $readStringJString
    convert toUnsafe $toUnsafe
    equals $testEquals
  """

  def readStringJString = prop { s: String =>
    JString(s).value must beEqualTo(s)
  }

  def toUnsafe = prop { b: Boolean =>
    scala.json.ast.JBoolean(b).toUnsafe == scala.json.ast.unsafe.JBoolean(b)
  }

  def testEquals = prop { s: String =>
    scala.json.ast.JString(s) == scala.json.ast.JString(s)
  }
}
