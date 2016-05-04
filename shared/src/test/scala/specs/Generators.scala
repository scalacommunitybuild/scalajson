/**
  * Shamelessly taken from
  * https://github.com/jeffmay/play-json-ops/tree/master/playJsonTests/src/main/scala/play/api/libs/json/scalacheck
  */

package specs

import java.math.MathContext

import org.scalacheck.Arbitrary._
import org.scalacheck.Gen._
import org.scalacheck.Shrink._
import org.scalacheck.{Arbitrary, Gen, Shrink}


object Generators {

  /**
    * The maximum number of fields of a [[scala.json.ast.JObject]] or elements of a [[scala.json.ast.JArray]] to construct when
    * generating one of these nested [[scala.json.ast.JValue]]s.
    */
  def defaultMaxWidth: Width = Width(2)

  /**
    * The maximum number of levels deep where nested values ([[JObject]]s or [[JArray]]s) can be generated.
    *
    * In other words:
    * - A depth of 0 generates only primitive [[JValue]]s
    * - A depth of 1 generates any type of [[JValue]] where all nested values contain only primitive [[JValue]]s.
    * - A depth of n generates any type of [[JValue]] where all nested values contain [[JValue]]s with a depth of n - 1
    */
  def defaultMaxDepth: Depth = Depth(2)

  implicit def arbJsValue(implicit
                          maxDepth: Depth = defaultMaxDepth,
                          maxWidth: Width = defaultMaxWidth): Arbitrary[scala.json.ast.JValue] = Arbitrary(genJsValue)

  implicit def arbJsObject(implicit
                           maxDepth: Depth = defaultMaxDepth,
                           maxWidth: Width = defaultMaxWidth): Arbitrary[scala.json.ast.JObject] = Arbitrary(genJsObject)

  implicit def arbJsArray(implicit
                          maxDepth: Depth = defaultMaxDepth,
                          maxWidth: Width = defaultMaxWidth): Arbitrary[scala.json.ast.JArray] = Arbitrary(genJsArray)

  implicit def arbJsString(implicit arbString: Arbitrary[String]): Arbitrary[scala.json.ast.JString] = Arbitrary {
    arbString.arbitrary map scala.json.ast.JString
  }

  implicit def arbJsNumber: Arbitrary[scala.json.ast.JNumber] = Arbitrary {
    genSafeBigDecimal map(x => scala.json.ast.JNumber(x.toString()))
  }

  implicit def arbJsBoolean: Arbitrary[scala.json.ast.JBoolean] = Arbitrary {
    Gen.oneOf(true, false) map(x => scala.json.ast.JBoolean(x))
  }

  /**
    * The Jaxson parser has trouble with very large exponents of BigDecimals.
    *
    * I couldn't quite pin the problem down to precision, scale, or both, so
    */
  def genSafeBigDecimal: Gen[BigDecimal] = {
    def chooseBigInt: Gen[BigInt] =
      sized((s: Int) => choose(-s, s)) map (x => BigInt(x))
    def genBigInt: Gen[BigInt] = Gen.frequency(
      (10, chooseBigInt),
      (1, BigInt(0)),
      (1, BigInt(1)),
      (1, BigInt(-1)),
      (1, BigInt(Int.MaxValue) + 1),
      (1, BigInt(Int.MinValue) - 1),
      (1, BigInt(Long.MaxValue)),
      (1, BigInt(Long.MinValue)),
      (1, BigInt(Long.MaxValue) + 1),
      (1, BigInt(Long.MinValue) - 1)
    )
    val mc = MathContext.DECIMAL128
    for {
      x <- genBigInt
      // Generates numbers outside the range of a Double without breaking the Jaxson parser.
      // I couldn't find the true source of the exception in Jaxson, but this should still cover most needs.
      scale <- Gen.chooseNum(10000, Int.MaxValue)
    } yield BigDecimal(x, scale, mc)
  }

  /**
    * Generates non-nested [[scala.json.ast.JValue]]s (ie. not [[scala.json.ast.JArray]] or [[scala.json.ast.JObject]]).
    */
  def genJsPrimitive: Gen[scala.json.ast.JValue] = {
    val genPrims: List[Gen[scala.json.ast.JValue]] = List(
      arbJsBoolean.arbitrary,
      arbJsNumber.arbitrary,
      arbJsString.arbitrary,
      Gen.const(scala.json.ast.JNull)
    )

    // A goofy way to match the signature of this method that I want, but it works
    Gen.oneOf(genPrims.head, genPrims.tail.head, genPrims.tail.tail: _*)
  }

  /**
    * Generates a primitive or nested [[scala.json.ast.JValue]] up to the specified depth and width
    *
    * @param maxDepth see [[defaultMaxDepth]] (cannot be less than 0)
    * @param maxWidth see [[defaultMaxWidth]] (cannot be less than 0)
    */
  def genJsValue(implicit maxDepth: Depth = defaultMaxDepth, maxWidth: Width = defaultMaxWidth): Gen[scala.json.ast.JValue] = {
    if (maxDepth === 0) genJsPrimitive
    else Gen.oneOf(
      genJsPrimitive,
      // The Scala compiler has a bug with AnyVal, where it favors implicits in the outer scope
      genJsArray(maxDepth, maxWidth),
      genJsObject(maxDepth, maxWidth)
    )
  }

  /**
    * Generates a nested array at the specified depth and width.
    *
    * @note the arrays may contain mixed type values at different depths, but never deeper than the [[defaultMaxDepth]].
    *
    * @param maxDepth see [[defaultMaxDepth]] (cannot be less than 1)
    * @param maxWidth see [[defaultMaxWidth]] (cannot be less than 1)
    */
  def genJsArray(implicit maxDepth: Depth = defaultMaxDepth, maxWidth: Width = defaultMaxWidth): Gen[scala.json.ast.JArray] =
    Gen.containerOfN[Vector,scala.json.ast.JValue](maxWidth, genJsValue(maxDepth - 1, maxWidth)) map { scala.json.ast.JArray.apply }

  /**
    * Generates a valid field name where the first character is alphabetical and the remaining chars
    * are alphanumeric.
    */
  def genFieldName: Gen[String] = Gen.identifier

  def genFields(implicit maxDepth: Depth = defaultMaxDepth, maxWidth: Width = defaultMaxWidth): Gen[(String, scala.json.ast.JValue)] = {
    // The Scala compiler has a bug with AnyVal, where it favors implicits in the outer scope
    Gen.zip(genFieldName, genJsValue(maxDepth, maxWidth))
  }

  /**
    * Generates a nested array at the specified depth and width.
    *
    * @param maxDepth see [[defaultMaxDepth]] (cannot be less than 1)
    * @param maxWidth see [[defaultMaxWidth]] (cannot be less than 1)
    */
  def genJsObject(implicit maxDepth: Depth = defaultMaxDepth, maxWidth: Width = defaultMaxWidth): Gen[scala.json.ast.JObject] = {
    for {
      fields <- Gen.listOfN(maxWidth, genFields(maxDepth - 1, maxWidth)).map(_.toMap)
    } yield scala.json.ast.JObject(fields)
  }

  // Shrinks for better error output

  implicit val shrinkJsArray: Shrink[scala.json.ast.JArray] = Shrink {
    arr =>
      val stream: Stream[scala.json.ast.JArray] = shrink(arr.value) map {x => scala.json.ast.JArray(x)}
      stream
  }

  implicit val shrinkJsObject: Shrink[scala.json.ast.JObject] = Shrink {
    obj =>
      val stream: Stream[scala.json.ast.JObject] = shrink(obj.value) map { fields => scala.json.ast.JObject(fields) }
      stream
  }

  implicit val shrinkJsValue: Shrink[scala.json.ast.JValue] = Shrink {
    case array: scala.json.ast.JArray => shrink(array)
    case obj: scala.json.ast.JObject  => shrink(obj)
    case scala.json.ast.JString(str)  => shrink(str) map scala.json.ast.JString
    case scala.json.ast.JNumber(num)  => shrink(num) map (x => scala.json.ast.JNumber(x))
    case scala.json.ast.JNull | scala.json.ast.JBoolean(_) => Stream.empty[scala.json.ast.JValue]
  }

}
