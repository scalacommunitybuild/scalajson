package specs

import org.scalacheck.{Arbitrary, Gen}

import scala.json.ast._
import scala.util.Random

object Generators {
  def jIntGenerator = Arbitrary.arbitrary[BigInt].map(scala.json.ast.JNumber.apply)

  def jStringGenerator = Arbitrary.arbitrary[String].map(scala.json.ast.JString.apply)

  def jBooleanGenerator = Arbitrary.arbitrary[Boolean].map(scala.json.ast.JBoolean.apply)

  def jArrayGenerator: Gen[JArray] =
    Gen.containerOf[Vector, JValue](jValueGenerator).map(scala.json.ast.JArray.apply)

  private def jObjectTypeGenerator: Gen[(String, JValue)] = for {
    string <- Arbitrary.arbitrary[String]
    jValue <- jValueGenerator
  } yield (string, jValue)

  def jObjectGenerator: Gen[JObject] =
    Gen.containerOf[List, (String, JValue)](jObjectTypeGenerator).map(data => scala.json.ast.JObject.apply(data.toMap))

  def jValueGenerator: Gen[JValue] = for {
    jInt <- jIntGenerator
    jString <- jStringGenerator
    jBoolean <- jBooleanGenerator
    jArray <- jArrayGenerator
    jObject <- jObjectGenerator
  } yield {
    val ran = Seq(jInt, jString, jBoolean, jArray, jObject)
    ran(Random.nextInt(ran.size))
  }

  implicit val arbitraryJValue = Arbitrary(jValueGenerator)

}
