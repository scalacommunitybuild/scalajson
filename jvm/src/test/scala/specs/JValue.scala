package specs

import Generators._

class JValue extends Spec {
  def is =
    s2"""
  The JArray value should
   equals $testEquals
  """

  def testEquals = prop { jValue: scala.json.ast.JValue =>
    // Is there a better way to do this?
    val cloned = jValue match {
      case scala.json.ast.JNull => scala.json.ast.JNull
      case jNumber: scala.json.ast.JNumber =>
        scala.json.ast.JNumber(jNumber.value)
      case jString: scala.json.ast.JString =>
        scala.json.ast.JString(jString.value)
      case jArray: scala.json.ast.JArray => scala.json.ast.JArray(jArray.value)
      case jObject: scala.json.ast.JObject =>
        scala.json.ast.JObject(jObject.value)
      case jBoolean: scala.json.ast.JBoolean =>
        scala.json.ast.JBoolean(jBoolean.get)
    }
    jValue must beEqualTo(cloned)
  }
}
