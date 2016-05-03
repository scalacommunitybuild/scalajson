package specs

import org.scalacheck.Prop._
import utest._

import scala.json.ast.JValue
import Generators._

import scala.scalajs.js
import js.JSConverters._

object JObject extends TestSuite with UTestScalaCheck {

  val tests = TestSuite {
//    "convert toUnsafe" - toUnsafe
//    "convert toUnsafe #2" - toUnsafe
  }

  def toUnsafe = forAll {jValue: JValue =>
    val a = scala.json.ast.JObject(Map("test" -> jValue)).toUnsafe
    val b = scala.json.ast.unsafe.JObject(
      js.Array(scala.json.ast.unsafe.JField("test",jValue.toUnsafe))
    )

    Utils.unsafeJValueEquals(a,b)
  }.checkUTest()

  def toUnsafe2 = forAll {jValues: Seq[JValue] =>
    val zipped = jValues.zipWithIndex
    val valuesAsMap = zipped.map{case (value,index) => (index.toString,value)}.toMap
    val valuesAsJField = zipped.map{case (value,index) =>
      scala.json.ast.unsafe.JField(index.toString,value.toUnsafe)
    }.toJSArray

    val a = scala.json.ast.JObject(valuesAsMap).toUnsafe
    val b = scala.json.ast.unsafe.JObject(
      valuesAsJField
    )

    Utils.unsafeJValueEquals(a,b)
  }.checkUTest()

}
