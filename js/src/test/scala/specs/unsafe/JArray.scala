package specs.unsafe

import org.scalacheck.Prop._

import scalajson.ast.unsafe, unsafe._
import Generators._
import specs.{UTestScalaCheck, Utils}
import utest._

import scala.scalajs.js
import js.JSConverters._

object JArray extends TestSuite with UTestScalaCheck {

  val tests = TestSuite {
    "The unsafe.JArray value should" - {
      "convert toStandard" - toStandard
      "have a useful toString" - _toString()
    }
  }

  def toStandard =
    forAll { jArray: scalajson.ast.unsafe.JArray =>
      val values = jArray.value.map(_.toStandard).toVector
      jArray.toStandard == scalajson.ast.JArray(values)
    }.checkUTest()

  def _toString() =
    "" + unsafe.JArray(JTrue, unsafe.JArray(JFalse)) ==>
      "JArray(JTrue,JArray(JFalse))"

}
