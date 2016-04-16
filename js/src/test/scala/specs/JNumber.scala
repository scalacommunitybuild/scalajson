package specs

import org.scalacheck.Prop.forAll
import utest._

object JNumber extends TestSuite with UTestScalaCheck {
  val tests = TestSuite {
    "The JNumber value should" - {
      "read a Long" - readLongJNumber
      "read a BigDecimal" - readBigDecimalJNumber
      "read a Double" - readDoubleJNumber
      "read a BigInt" - readBigIntJNumber
      "read an Int" - readIntJNumber
      "read a Double NaN" - readDoubleNANJNumber
      "read a Double Positive Infinity" - readDoublePositiveInfinityJNumber
      "read a Double Negative Infinity" - readDoubleNegativeInfinityJNumber
      "read a Float" - readFloatJNumber
      "read a Short" - readShortJNumber
      "convert to jsAny" - toJsAny
    }

    def readLongJNumber = forAll { l: Long =>
      scala.json.ast.JNumber(l).value == l.toString
    }.checkUTest()

    def readBigDecimalJNumber = forAll { b: BigDecimal =>
      scala.json.ast.JNumber(b).value == b.toString()
    }.checkUTest()

    def readBigIntJNumber = forAll { b: BigInt =>
      scala.json.ast.JNumber(b).value == b.toString
    }.checkUTest()

    def readIntJNumber = forAll { i: Int =>
      scala.json.ast.JNumber(i).value == i.toString
    }.checkUTest()

    def readDoubleJNumber = forAll {d: Double =>
      scala.json.ast.JNumber(d) match {
        case scala.json.ast.JNull => JNull == JNull
        case scala.json.ast.JNumber(value) => value == d.toString
      }
    }.checkUTest()

    def readDoubleNANJNumber = {
      scala.json.ast.JNumber(Double.NaN) match {
        case scala.json.ast.JNull => true
        case _ => false
      }
    }

    def readDoublePositiveInfinityJNumber = {
      scala.json.ast.JNumber(Double.PositiveInfinity) match {
        case scala.json.ast.JNull => true
        case _ => false
      }
    }

    def readDoubleNegativeInfinityJNumber = {
      scala.json.ast.JNumber(Double.NegativeInfinity) match {
        case scala.json.ast.JNull => true
        case _ => false
      }
    }

    def readFloatJNumber = forAll { f: Float =>
      scala.json.ast.JNumber(f).value == f.toString
    }.checkUTest()

    def readShortJNumber = forAll { s: Short =>
      scala.json.ast.JNumber(s).value == s.toString
    }.checkUTest()

    def toJsAny = forAll {d: Double =>
      scala.json.ast.JNumber(d).toJsAny == d
    }.checkUTest()
  }
}
