package benchmark.safe

import org.scalameter._
import scala.json.ast.safe._

object Generators {
  def jBoolean: Gen[JBoolean] = for {
    size <- Gen.range("seed")(300000, 1500000, 300000)
  } yield {
    if (size % 2 == 0)
      JBoolean(true)
    else
      JBoolean(false)
  }

  def jString: Gen[JString] = for {
    size <- Gen.range("seed")(300000, 1500000, 300000)
  } yield {
    JString(size.toString)
  }

  def jNumber: Gen[JNumber] = for {
    size <- Gen.range("seed")(300000, 1500000, 300000)
  } yield {
    scala.json.ast.safe.JNumber(size)
  }

  def jArray: Gen[JArray] = for {
    size <- Gen.range("seed")(0, 10, 1)
    randomJValue <- jValue
  } yield {

    var index = 0
    val b = Vector.newBuilder[scala.json.ast.safe.JValue]
    while (index < size) {
      b += randomJValue
      index = index + 1
    }
    scala.json.ast.safe.JArray(b.result())
  }

  def jObject: Gen[JObject] = for {
    size <- Gen.range("seed")(0, 10, 1)
    string <- Gen.range("seed")(300000, 1500000, 300000).map {
      _.toString
    }
    randomJValue <- jValue
  } yield {

    var index = 0
    val b = Map.newBuilder[String, scala.json.ast.safe.JValue]

    (0 until size).foreach { _ =>
      b += ((string, randomJValue))
    }

    scala.json.ast.safe.JObject(b.result())
  }

  def jValue: Gen[JValue] = Gen.range("JValue type")(300000, 1500000, 300000).flatMap { seed =>
    seed % 5 match {
      case 0 => jBoolean.asInstanceOf[Gen[JValue]]
      case 1 => jString.asInstanceOf[Gen[JValue]]
      case 2 => jNumber.asInstanceOf[Gen[JValue]]
      case 3 => jArray.asInstanceOf[Gen[JValue]]
      case 4 => JArray.asInstanceOf[Gen[JValue]]
    }
  }

}
