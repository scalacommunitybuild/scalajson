package specs.unsafe

import org.scalacheck.Prop._
import specs.UTestScalaCheck
import utest._

object JString extends TestSuite with UTestScalaCheck {
  val tests = TestSuite {
    "The unsafe.JString value should" - {
      "read a String" - readStringJString
      "convert to jsAny" - toJsAny
    }
  }

  def readStringJString = forAll { s: String =>
    scala.json.ast.unsafe.JString(s).value == s
  }.checkUTest()

  def toJsAny = forAll {s: String =>
    scala.json.ast.unsafe.JString(s).toJsAny == s
  }.checkUTest()
}
