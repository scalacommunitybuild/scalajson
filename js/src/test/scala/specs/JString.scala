package specs

import utest.TestSuite
import utest._
import org.scalacheck.Prop.forAll

object JString extends TestSuite with UTestScalaCheck {
  val tests = TestSuite {
    "The JString value should" - {
      "read a String" - readStringJString
      "convert To jsAny" - toJsAny
    }

    def readStringJString = forAll { s: String =>
      scala.json.ast.JString(s).value == s
    }.checkUTest()

    def toJsAny = forAll {s: String =>
      scala.json.ast.JString(s).toJsAny == s
    }.checkUTest()
  }
}
