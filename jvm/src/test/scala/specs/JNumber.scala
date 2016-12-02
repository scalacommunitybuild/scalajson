package specs

import scala.json.ast._

class JNumber extends Spec {
  def is =
    s2"""
  The JNumber value should
    read a Long $readLongJNumber
    read a BigDecimal $readBigDecimalJNumber
    read a BigInt $readBigIntJNumber
    read an Int $readIntJNumber
    read a Double $readDoubleJNumber
    read a Double NaN $readDoubleNANJNumber
    read a Double Positive Infinity $readDoublePositiveInfinityJNumber
    read a Double Negative Infinity $readDoubleNegativeInfinityJNumber
    read a Float $readFloatJNumber
    read a Short $readShortJNumber
    hashCode equals decimal $hashCodeEqualsDecimal
    hashCode equals decimal #2 $hashCodeEqualsDecimal2
    hashCode not equals decimal $hashCodeNotEqualsDecimal
    hashCode not equals decimal #2 $hashCodeNotEqualsDecimal2
    hashCode equals e $hashCodeEqualsE
    hashCode equals e #2 $hashCodeEqualsE2
    hashCode equals e negative $hashCodeEqualsENegative
    hashCode equals e negative #2 $hashCodeEqualsENegative2
    hashCode not equals e negative $hashCodeNotEqualsENegative
    hashCode not equals e negative #2 $hashCodeNotEqualsENegative2
    hashCode equals e positive $hashCodeEqualsEPositive
    hashCode equals e positive #2 $hashCodeEqualsEPositive2
    hashCode not equals e positive $hashCodeNotEqualsEPositive
    hashCode not equals e positive #2 $hashCodeNotEqualsEPositive2
    convert toUnsafe $toUnsafe
    equals $testEquals
  """

  def readLongJNumber = prop { l: Long =>
    JNumber(l).value must beEqualTo(l.toString)
  }

  def readBigDecimalJNumber = prop { b: BigDecimal =>
    JNumber(b).value must beEqualTo(b.toString())
  }

  def readBigIntJNumber = prop { b: BigInt =>
    JNumber(b).value must beEqualTo(b.toString)
  }

  def readIntJNumber = prop { i: Int =>
    JNumber(i).value must beEqualTo(i.toString)
  }

  def readDoubleJNumber = prop { d: Double =>
    JNumber(d) match {
      case JNull => JNull must beEqualTo(JNull)
      case JNumber(value) => value must beEqualTo(d.toString)
    }
  }

  def readDoubleNANJNumber = {
    JNumber(Double.NaN) match {
      case JNull => true
      case _ => false
    }
  }

  def readDoublePositiveInfinityJNumber = {
    JNumber(Double.PositiveInfinity) match {
      case JNull => true
      case _ => false
    }
  }

  def readDoubleNegativeInfinityJNumber = {
    JNumber(Double.NegativeInfinity) match {
      case JNull => true
      case _ => false
    }
  }

  def readFloatJNumber = prop { f: Float =>
    JNumber(f).value must beEqualTo(f.toString)
  }

  def readShortJNumber = prop { s: Short =>
    JNumber(s).value must beEqualTo(s.toString)
  }

  def hashCodeEqualsDecimal = {
    JNumber("34").## must beEqualTo(JNumber("34.0").##)
  }

  def hashCodeEqualsDecimal2 = {
    JNumber("34").## must beEqualTo(JNumber("34.00").##)
  }

  def hashCodeNotEqualsDecimal = {
    JNumber("34").## mustNotEqual JNumber("34.01").##
  }

  def hashCodeNotEqualsDecimal2 = {
    JNumber("34").## mustNotEqual JNumber("34.001").##
  }

  def hashCodeEqualsE = {
    JNumber("34e34").## must beEqualTo(JNumber("34e034").##)
  }

  def hashCodeEqualsE2 = {
    JNumber("34e34").## must beEqualTo(JNumber("34e0034").##)
  }

  def hashCodeEqualsENegative = {
    JNumber("34e-0").## must beEqualTo(JNumber("34").##)
  }

  def hashCodeEqualsENegative2 = {
    JNumber("34e-00").## must beEqualTo(JNumber("34").##)
  }

  def hashCodeNotEqualsENegative = {
    JNumber("34e-01").## mustNotEqual JNumber("34").##
  }

  def hashCodeNotEqualsENegative2 = {
    JNumber("34e-001").## mustNotEqual JNumber("34").##
  }

  def hashCodeEqualsEPositive = {
    JNumber("34e+0").## must beEqualTo(JNumber("34").##)
  }

  def hashCodeEqualsEPositive2 = {
    JNumber("34e+00").## must beEqualTo(JNumber("34").##)
  }

  def hashCodeNotEqualsEPositive = {
    JNumber("34e+01").## mustNotEqual JNumber("34").##
  }

  def hashCodeNotEqualsEPositive2 = {
    JNumber("34e+001").## mustNotEqual JNumber("34").##
  }

  def toUnsafe = prop { b: BigDecimal =>
    scala.json.ast.JNumber(b).toUnsafe must beEqualTo(
      scala.json.ast.unsafe.JNumber(b))
  }

  def testEquals = prop { b: BigDecimal =>
    scala.json.ast.JNumber(b) must beEqualTo(scala.json.ast.JNumber(b))
  }
}
