package specs

import utest.TestSuite
import utest._
import org.scalacheck.Prop.forAll

object JString extends TestSuite with UTestScalaCheck {

  val tests = TestSuite {
    "The JString value should" - {
      "read a String" - readStringJString
      "convert to jsAny" - toJsAny
      "convert toUnsafe" - toUnsafe
      "equals" - testEquals
    }

    def readStringJString =
      forAll { s: String =>
        scalajson.ast.JString(s).value == s
      }.checkUTest()

    def toJsAny =
      forAll { s: String =>
        scalajson.ast.JString(s).toJsAny == s
      }.checkUTest()

    def toUnsafe =
      forAll { b: Boolean =>
        scalajson.ast.JBoolean(b).toUnsafe == scalajson.ast.unsafe
          .JBoolean(b)
      }.checkUTest()

    def testEquals =
      forAll { s: String =>
        scalajson.ast.JString(s) == scalajson.ast.JString(s)
      }.checkUTest()
  }
}
