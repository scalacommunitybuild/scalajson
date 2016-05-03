package specs

import org.scalacheck.Prop._
import utest._

import scala.json.ast.JValue
import Generators._
import scala.scalajs.js
import js.JSConverters._

object JArray extends TestSuite with UTestScalaCheck {
  val tests = TestSuite {
//    "convert toUnsafe" - toUnsafe
//    "convert toUnsafe #2" - toUnsafe2
  }

  def toUnsafe = forAll {jValue: JValue =>
    scala.json.ast.JArray(jValue).toUnsafe == scala.json.ast.unsafe.JArray(jValue.toUnsafe)
  }.checkUTest()

  def toUnsafe2 = forAll{jValues: Seq[JValue] =>
    scala.json.ast.JArray(jValues.to[Vector]).toUnsafe == scala.json.ast.unsafe.JArray(jValues.map(_.toUnsafe).toJSArray)
  }.checkUTest()
}
