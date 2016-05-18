package specs.unsafe

import specs.UTestScalaCheck
import utest._
import org.scalacheck.Prop._

object JNumber extends TestSuite with UTestScalaCheck {

  val tests = TestSuite {
    "The unsafe.JNumber value should" - {
      "read a Long" - readLongJNumber
      "read a BigDecimal" - readBigDecimalJNumber
      "read a BigInt" - readBigIntJNumber
      "read an Int" - readIntJNumber
      "read a Double" - readDoubleJNumber
      "read a Double NaN" - readDoubleNANJNumber
      "read a Double Positive Infinity" - readDoublePositiveInfinityJNumber
      "read a Double Negative Infinity" - readDoubleNegativeInfinityJNumber
      "read a Float" - readFloatJNumber
      "read a Short" - readShortJNumber
      "read a String and not fail" - readStringJNumber
      "read a String and detect non numeric numbers" - readStringJNumberDetect
      "convert to jsAny" - toJsAny
      "convert toStandard" - toStandard
    }
  }

  def readLongJNumber =
    forAll { l: Long =>
      scala.json.ast.unsafe.JNumber(l).value == l.toString
    }.checkUTest()

  def readBigDecimalJNumber =
    forAll { b: BigDecimal =>
      scala.json.ast.unsafe.JNumber(b).value == b.toString()
    }.checkUTest()

  def readBigIntJNumber =
    forAll { b: BigInt =>
      scala.json.ast.unsafe.JNumber(b).value == b.toString()
    }.checkUTest()

  def readIntJNumber =
    forAll { i: Int =>
      scala.json.ast.unsafe.JNumber(i).value == i.toString
    }.checkUTest()

  def readDoubleJNumber =
    forAll { d: Double =>
      scala.json.ast.unsafe.JNumber(d).value == d.toString
    }.checkUTest()

  def readDoubleNANJNumber = {
    scala.json.ast.unsafe.JNumber(Double.NaN).value match {
      case "NaN" => true
      case _ => false
    }
  }

  def readDoublePositiveInfinityJNumber = {
    scala.json.ast.unsafe.JNumber(Double.PositiveInfinity).value match {
      case "Infinity" => true
      case _ => false
    }
  }

  def readDoubleNegativeInfinityJNumber = {
    scala.json.ast.unsafe.JNumber(Double.NegativeInfinity).value match {
      case "-Infinity" => true
      case _ => false
    }
  }

  def readFloatJNumber =
    forAll { f: Float =>
      scala.json.ast.unsafe.JNumber(f).value == f.toString
    }.checkUTest()

  def readShortJNumber =
    forAll { s: Short =>
      scala.json.ast.unsafe.JNumber(s).value == s.toString
    }.checkUTest()

  def readStringJNumber =
    forAll { s: String =>
      scala.json.ast.unsafe.JNumber(s).value == s.toString
    }.checkUTest()

  def readStringJNumberDetect =
    forAll { s: String =>
      {
        scala.util.Try {
          BigDecimal(s)
        }.toOption.isEmpty
      } ==> {
        scala.util
          .Try(BigDecimal(scala.json.ast.unsafe.JNumber(s).value))
          .toOption
          .isEmpty == true
      }
    }.checkUTest()

  def toJsAny =
    forAll { d: Double =>
      scala.json.ast.unsafe.JNumber(d).toJsAny == d
    }.checkUTest()

  def toStandard =
    forAll { b: BigDecimal =>
      scala.json.ast.unsafe.JNumber(b).toStandard == scala.json.ast.JNumber(b)
    }.checkUTest()
}
