package specs.unsafe

import org.scalacheck.Prop._
import utest._

import scalajson.ast.unsafe, unsafe._
import Generators._
import specs.{UTestScalaCheck, Utils}

import scala.scalajs.js
import js.JSConverters._

object JObject extends TestSuite with UTestScalaCheck {

  val tests = TestSuite {
    "The unsafe.JObject value should" - {
      "convert toStandard" - toStandard
      "have a useful toString" - _toString()
    }
  }

  def toStandard =
    forAll { jObject: scalajson.ast.unsafe.JObject =>
      val values = jObject.value.map { x =>
        (x.field, x.value.toStandard)
      }.toMap

      jObject.toStandard == scalajson.ast.JObject(values)
    }.checkUTest()

  def _toString() =
    "" + unsafe.JObject(JField("a", unsafe.JObject(JField("b", JFalse)))) ==>
      "JObject(JField(a,JObject(JField(b,JFalse))))"

}
