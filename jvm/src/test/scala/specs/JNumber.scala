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

}
