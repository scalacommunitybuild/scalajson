package specs.unsafe

import org.scalacheck.Prop._
import specs.UTestScalaCheck
import utest._

import Generators._

object JValue extends TestSuite with UTestScalaCheck {
  val tests = TestSuite {
    "The JValue value should" - {
      "equals" - testEquals
    }
  }

  def testEquals =
    forAll { jValue: scala.json.ast.unsafe.JValue =>
      // Is there a better way to do this?
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
      jValue == cloned
    }.checkUTest()
}
