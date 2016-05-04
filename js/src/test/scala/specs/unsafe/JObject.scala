package specs.unsafe

import org.scalacheck.Prop._
import utest._

import scala.json.ast._
import Generators._
import specs.{UTestScalaCheck, Utils}

import scala.scalajs.js
import js.JSConverters._

object JObject extends TestSuite with UTestScalaCheck {
  val tests = TestSuite {
    "convert toStandard" - toStandard
  }

  def toStandard = forAll { jObject: scala.json.ast.unsafe.JObject =>
    val values = jObject.value.map{x =>
      (x.field,x.value.toStandard)
    }.toMap

   jObject.toStandard == scala.json.ast.JObject(values)
  }.checkUTest()

}
