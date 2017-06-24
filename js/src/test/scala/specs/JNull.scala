package specs

import utest._

object JNull extends TestSuite with UTestScalaCheck {

  val tests = TestSuite {
    "The JNull value should" - {
      "convert to jsAny" - toJsAny
      "convert toUnsafe" - toUnsafe
      "equals" - testEquals
    }
  }

  def toJsAny = {
    scalajson.ast.JNull.toJsAny == null
  }

  def toUnsafe = {
    scalajson.ast.JNull.toUnsafe == scalajson.ast.unsafe.JNull
  }

  def testEquals = {
    scalajson.ast.JNull == scalajson.ast.JNull
  }
}
