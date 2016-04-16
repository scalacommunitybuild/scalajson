package specs

import utest._

object JNull extends TestSuite with UTestScalaCheck {
  val tests = TestSuite {
    "The JNull value should" - {
      "convert To jsAny" - toJsAny
    }
  }

  def toJsAny = {
    scala.json.ast.JNull.toJsAny == null
  }

}
