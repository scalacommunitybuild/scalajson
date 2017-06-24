package specs

import org.scalacheck.Prop._
import utest._

import scalajson.ast._
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
    forAll { jObject: scalajson.ast.JObject =>
      val values = jObject.value.map {
        case (k, v) =>
          scalajson.ast.unsafe.JField(k, v.toUnsafe)
      }
      Utils.unsafeJValueEquals(jObject.toUnsafe,
                               scalajson.ast.unsafe.JObject(values.toJSArray))
    }.checkUTest()

  def testEquals =
    forAll { jObject: scalajson.ast.JObject =>
      scalajson.ast.JObject(jObject.value) == scalajson.ast.JObject(
        jObject.value)
    }.checkUTest()
}
