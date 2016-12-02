package specs

import org.scalacheck.Prop._
import utest._

import scala.json.ast._
import Generators._

import scala.scalajs.js
import js.JSConverters._

object JObject extends TestSuite with UTestScalaCheck {

  val tests = TestSuite {
    "The JObject value should" - {
      "convert toUnsafe" - toUnsafe
      "equals" - testEquals
    }
  }

  def toUnsafe =
    forAll { jObject: scala.json.ast.JObject =>
      val values = jObject.value.map {
        case (k, v) =>
          scala.json.ast.unsafe.JField(k, v.toUnsafe)
      }
      Utils.unsafeJValueEquals(jObject.toUnsafe,
                               scala.json.ast.unsafe.JObject(values.toJSArray))
    }.checkUTest()

  def testEquals =
    forAll { jObject: scala.json.ast.JObject =>
      scala.json.ast.JObject(jObject.value) == scala.json.ast.JObject(
        jObject.value)
    }.checkUTest()
}
