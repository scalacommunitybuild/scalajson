package specs.unsafe

import org.scalacheck.Prop._

import scala.json.ast._
import Generators._
import specs.{UTestScalaCheck, Utils}
import utest._

import scala.scalajs.js
import js.JSConverters._

object JArray extends TestSuite with UTestScalaCheck {

  val tests = TestSuite {
    "The unsafe.JArray value should" - {
      "convert toStandard" - toStandard
    }
  }

  def toStandard =
    forAll { jArray: scala.json.ast.unsafe.JArray =>
      val values = jArray.value.map(_.toStandard).to[Vector]
      jArray.toStandard == scala.json.ast.JArray(values)
    }.checkUTest()
}
