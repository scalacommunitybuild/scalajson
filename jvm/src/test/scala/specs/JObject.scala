package specs

import Generators._

class JObject extends Spec {
  def is =
    s2"""
  The JArray value should
   convert toUnsafe $toUnsafe
   equals $testEquals
  """

  def toUnsafe = prop {jObject: scala.json.ast.JObject =>
    val values = jObject.value.map{case (k,v) =>
      scala.json.ast.unsafe.JField(k,v.toUnsafe)
    }
    Utils.unsafeJValueEquals(jObject.toUnsafe,scala.json.ast.unsafe.JObject(values.toArray))
  }

  def testEquals = prop {jObject: scala.json.ast.JObject =>
    scala.json.ast.JObject(jObject.value) must beEqualTo(scala.json.ast.JObject(jObject.value))
  }
}
