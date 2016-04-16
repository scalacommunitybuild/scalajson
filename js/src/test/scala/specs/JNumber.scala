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
      "hashCode equals decimal" - hashCodeEqualsDecimal
      "hashCode equals decimal #2" - hashCodeEqualsDecimal2
      "hashCode not equals decimal" - hashCodeNotEqualsDecimal
      "hashCode not equals decimal #2" - hashCodeNotEqualsDecimal2
      "hashCode equals e" - hashCodeEqualsE
      "hashCode equals e #2" - hashCodeEqualsE2
      "convert to jsAny" - toJsAny
      "hashCode equals e negative" - hashCodeEqualsENegative
      "hashCode equals e negative" - hashCodeEqualsENegative2
      "hashCode not equals e negative" - hashCodeNotEqualsENegative
      "hashCode not equals e negative #2" - hashCodeNotEqualsENegative2
      "hashCode equals e positive" - hashCodeEqualsEPositive
      "hashCode equals e positive #2" - hashCodeEqualsEPositive2
      "hashCode not equals e positive" - hashCodeNotEqualsEPositive
      "hashCode not equals e positive #2" - hashCodeNotEqualsEPositive2
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

    def hashCodeEqualsDecimal = {
      scala.json.ast.JNumber("34").## == scala.json.ast.JNumber("34.0").##
    }

    def hashCodeEqualsDecimal2 = {
      scala.json.ast.JNumber("34").## == scala.json.ast.JNumber("34.00").##
    }

    def hashCodeNotEqualsDecimal = {
      scala.json.ast.JNumber("34").## == scala.json.ast.JNumber("34.01").##
    }

    def hashCodeNotEqualsDecimal2 = {
      scala.json.ast.JNumber("34").## == scala.json.ast.JNumber("34.001").##
    }

    def hashCodeEqualsE = {
      scala.json.ast.JNumber("34e34").## != scala.json.ast.JNumber("34e034").##
    }

    def hashCodeEqualsE2 = {
      scala.json.ast.JNumber("34e34").## != scala.json.ast.JNumber("34e0034").##
    }

    def hashCodeEqualsENegative = {
      JNumber("34e-0").## == JNumber("34").##
    }

    def hashCodeEqualsENegative2 = {
      JNumber("34e-00").## == JNumber("34").##
    }

    def hashCodeNotEqualsENegative = {
      JNumber("34e-01").## != JNumber("34").##
    }

    def hashCodeNotEqualsENegative2 = {
      JNumber("34e-001").## != JNumber("34").##
    }

    def hashCodeEqualsEPositive = {
      JNumber("34e+0").## == JNumber("34").##
    }

    def hashCodeEqualsEPositive2 = {
      JNumber("34e+00").## == JNumber("34").##
    }

    def hashCodeNotEqualsEPositive = {
      JNumber("34e+01").## != JNumber("34").##
    }

    def hashCodeNotEqualsEPositive2 = {
      JNumber("34e+001").## != JNumber("34").##
    }

    def toJsAny = forAll {d: Double =>
      scala.json.ast.JNumber(d).toJsAny == d
    }.checkUTest()
  }
}
