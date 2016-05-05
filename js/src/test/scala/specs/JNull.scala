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
    scala.json.ast.JNull.toJsAny == null
  }

  def toUnsafe = {
    scala.json.ast.JNull.toUnsafe == scala.json.ast.unsafe.JNull
  }

  def testEquals = {
    scala.json.ast.JNull == scala.json.ast.JNull
  }

}
