package benchmark.fast

import org.scalameter._

import scala.json.ast.fast._

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
    JNumber(size)
  }

  def jArray: Gen[JArray] = for {
    size <- Gen.range("seed")(0, 10, 1)
    randomJValue <- jValue
  } yield {
    
    val array: Array[JValue] = Array.ofDim(size)
    (0 until size).foreach{index =>
      array(index) = randomJValue
    }
    scala.json.ast.fast.JArray(array)
  }
  
  def jObject: Gen[JObject] = for {
    size <- Gen.range("seed")(0, 10, 1)
    string <- Gen.range("seed")(300000, 1500000, 300000).map{_.toString}
    randomJValue <- jValue
  } yield {
    val array: Array[JField] = Array.ofDim(size)
    (0 until size).foreach{index =>
      array(index) = JField(string,randomJValue)
    }
    scala.json.ast.fast.JObject(array)
  }
  
  def jValue: Gen[JValue] = Gen.range("JValue type")(300000, 1500000, 300000).flatMap{ randomSeed =>
    randomSeed %5 match {
      case 0 => jBoolean.asInstanceOf[Gen[JValue]]
      case 1 => jString.asInstanceOf[Gen[JValue]]
      case 2 => jNumber.asInstanceOf[Gen[JValue]]
      case 3 => jArray.asInstanceOf[Gen[JValue]]
      case 4 => JArray.asInstanceOf[Gen[JValue]]
    }
  }
  

  
}
