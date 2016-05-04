package specs

import org.scalacheck.Prop._
import utest._

object JBoolean extends TestSuite with UTestScalaCheck {
  val tests = TestSuite {
    "The JBoolean value should" - {
      "read a Boolean" - readBooleanJBoolean
      "pattern match with JTrue" - readBooleanJBooleanPatternMatchJBooleanTrue
      "pattern match with JTrue and fail with scala.MatchError" - readBooleanJBooleanPatternMatchJBooleanTrueFail
      "pattern match with JFalse" - readBooleanJBooleanPatternMatchJFalse
      "pattern match with JFalse and fail with scala.MatchError" - readBooleanJBooleanPatternMatchJFalseFail
      "pattern match with JBoolean as true" - readBooleanJBooleanPatternMatchJBooleanTrue
      "pattern match with JBoolean as true and fail with scala.MatchError" - readBooleanJBooleanPatternMatchJBooleanTrueFail
      "pattern match with JBoolean as false" - readBooleanJBooleanPatternMatchJBooleanFalse
      "pattern match with JBoolean as false and fail with scala.MatchError" - readBooleanJBooleanPatternMatchJBooleanFalseFail
      "The JTrue value should read a Boolean as true" - readBooleanJTrue
      "The JFalse value should read a Boolean as false" - readBooleanJFalse
      "equals" - testEquals
      "convert to jsAny" - toJsAny
      "convert toUnsafe" - toUnsafe
    }
  }

  def readBooleanJBoolean = forAll { b: Boolean =>
    scala.json.ast.JBoolean(b).get == b
  }.checkUTest()

  def readBooleanJBooleanPatternMatchJBooleanTrue = forAll { b: Boolean => {
    b == true
  } ==> {
    val result = scala.json.ast.JBoolean(b) match {
      case f@scala.json.ast.JBoolean(true) => f
    }
    result.get == b
  }
  }.checkUTest()

  def readBooleanJBooleanPatternMatchJBooleanTrueFail = {
    try {
      scala.json.ast.JBoolean(true) match {
        case f@scala.json.ast.JBoolean(false) => f
      }
    } catch {
      case _: MatchError => true
      case _ => false
    }
  }

  def readBooleanJBooleanPatternMatchJBooleanFalse = forAll { b: Boolean => {
    b == false
  } ==> {
    val result = scala.json.ast.JBoolean(b) match {
      case f@scala.json.ast.JBoolean(false) => f
    }
    result.get == b
  }
  }.checkUTest()

  def readBooleanJBooleanPatternMatchJBooleanFalseFail = {
    try {
      scala.json.ast.JBoolean(false) match {
        case f@scala.json.ast.JBoolean(true) => f
      }
    } catch {
      case _: MatchError => true
      case _ => false
    }
  }

  def readBooleanJBooleanPatternMatchJTrue = forAll { b: Boolean =>
    (b == true) ==> {
      val result = scala.json.ast.JBoolean(b) match {
        case f@scala.json.ast.JTrue => f
      }
      result.get == b
    }
  }.checkUTest()

  def readBooleanJBooleanPatternMatchJTrueFail = {
    try {
      scala.json.ast.JBoolean(true) match {
        case f@scala.json.ast.JFalse => f
      }
    } catch {
      case _: MatchError => true
      case _ => false
    }
  }

  def readBooleanJBooleanPatternMatchJFalse = forAll { b: Boolean =>
    (b == false) ==> {
      val result = scala.json.ast.JBoolean(b) match {
        case f@scala.json.ast.JFalse => f
      }
      result.get == b
    }
  }.checkUTest()

  def readBooleanJBooleanPatternMatchJFalseFail = {
    try {
      scala.json.ast.JBoolean(false) match {
        case f@scala.json.ast.JTrue => f
      }
    } catch {
      case _: MatchError => true
      case _ => false
    }
  }

  def readBooleanJTrue = forAll { b: Boolean =>
    (b == true) ==> {
      scala.json.ast.JTrue.get == b
    }
  }.checkUTest()

  def readBooleanJFalse = forAll { b: Boolean =>
    (b == false) ==> {
      scala.json.ast.JFalse.get == b
    }
  }.checkUTest()

  def testEquals = forAll { b: Boolean =>
    scala.json.ast.JBoolean(b) == scala.json.ast.JBoolean(b)
  }.checkUTest()

  def toJsAny = forAll {b: Boolean =>
    scala.json.ast.JBoolean(b).toJsAny == b
  }.checkUTest()

  def toUnsafe = forAll { b: Boolean =>
    scala.json.ast.JBoolean(b).toUnsafe == scala.json.ast.unsafe.JBoolean(b)
  }.checkUTest()
}
