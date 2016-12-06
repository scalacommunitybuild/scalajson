package specs.unsafe

import specs.Spec
import scala.json.ast.unsafe._
import Generators._

class JValue extends Spec {
  def is =
    s2"""
  The JValue value should
   equals $testEquals
  """

  def testEquals = prop { jValue: scala.json.ast.unsafe.JValue =>
    val cloned = jValue match {
      case scala.json.ast.unsafe.JNull => scala.json.ast.unsafe.JNull
      case jNumber: scala.json.ast.unsafe.JNumber =>
        scala.json.ast.unsafe.JNumber(jNumber.value)
      case jString: scala.json.ast.unsafe.JString =>
        scala.json.ast.unsafe.JString(jString.value)
      case jArray: scala.json.ast.unsafe.JArray =>
        scala.json.ast.unsafe.JArray(jArray.value)
      case jObject: scala.json.ast.unsafe.JObject =>
        scala.json.ast.unsafe.JObject(jObject.value)
      case jBoolean: scala.json.ast.unsafe.JBoolean =>
        scala.json.ast.unsafe.JBoolean(jBoolean.get)
    }
    jValue must beEqualTo(cloned)
  }
}
