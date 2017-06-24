package specs

import scalajson.ast.JString

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
    scalajson.ast.JBoolean(b).toUnsafe == scalajson.ast.unsafe.JBoolean(b)
  }

  def testEquals = prop { s: String =>
    scalajson.ast.JString(s) == scalajson.ast.JString(s)
  }
}
