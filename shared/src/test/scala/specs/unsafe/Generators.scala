/**
  * Shamelessly taken from
  * https://github.com/jeffmay/play-json-ops/tree/master/playJsonTests/src/main/scala/play/api/libs/json/scalacheck
  */

package specs.unsafe

import java.math.MathContext
import specs._
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen._
import org.scalacheck.Shrink._
import org.scalacheck.{Arbitrary, Gen, Shrink}

object Generators {

  /**
    * The maximum number of fields of a [[scala.json.ast.unsafe.JObject]] or elements of a [[scala.json.ast.unsafe.JArray]] to construct when
    * generating one of these nested [[scala.json.ast.unsafe.JValue]]s.
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
                          maxWidth: Width = defaultMaxWidth): Arbitrary[scala.json.ast.unsafe.JValue] = Arbitrary(genJsValue)

  implicit def arbJsObject(implicit
                           maxDepth: Depth = defaultMaxDepth,
                           maxWidth: Width = defaultMaxWidth): Arbitrary[scala.json.ast.unsafe.JObject] = Arbitrary(genJsObject)

  implicit def arbJsArray(implicit
                          maxDepth: Depth = defaultMaxDepth,
                          maxWidth: Width = defaultMaxWidth): Arbitrary[scala.json.ast.unsafe.JArray] = Arbitrary(genJsArray)

  implicit def arbJsString(implicit arbString: Arbitrary[String]): Arbitrary[scala.json.ast.unsafe.JString] = Arbitrary {
    arbString.arbitrary map scala.json.ast.unsafe.JString
  }

  implicit def arbJsNumber: Arbitrary[scala.json.ast.unsafe.JNumber] = Arbitrary {
    genSafeBigDecimal map(x => scala.json.ast.unsafe.JNumber(x.toString()))
  }

  implicit def arbJsBoolean: Arbitrary[scala.json.ast.unsafe.JBoolean] = Arbitrary {
    Gen.oneOf(true, false) map(x => scala.json.ast.unsafe.JBoolean(x))
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
    * Generates non-nested [[scala.json.ast.unsafe.JValue]]s (ie. not [[scala.json.ast.unsafe.JArray]] or [[scala.json.ast.unsafe.JObject]]).
    */
  def genJsPrimitive: Gen[scala.json.ast.unsafe.JValue] = {
    val genPrims: List[Gen[scala.json.ast.unsafe.JValue]] = List(
      arbJsBoolean.arbitrary,
      arbJsNumber.arbitrary,
      arbJsString.arbitrary,
      Gen.const(scala.json.ast.unsafe.JNull)
    )

    // A goofy way to match the signature of this method that I want, but it works
    Gen.oneOf(genPrims.head, genPrims.tail.head, genPrims.tail.tail: _*)
  }

  /**
    * Generates a primitive or nested [[scala.json.ast.unsafe.JValue]] up to the specified depth and width
    *
    * @param maxDepth see [[defaultMaxDepth]] (cannot be less than 0)
    * @param maxWidth see [[defaultMaxWidth]] (cannot be less than 0)
    */
  def genJsValue(implicit maxDepth: Depth = defaultMaxDepth, maxWidth: Width = defaultMaxWidth): Gen[scala.json.ast.unsafe.JValue] = {
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
  def genJsArray(implicit maxDepth: Depth = defaultMaxDepth, maxWidth: Width = defaultMaxWidth): Gen[scala.json.ast.unsafe.JArray] =
    Gen.containerOfN[Vector,scala.json.ast.unsafe.JValue](maxWidth, genJsValue(maxDepth - 1, maxWidth)) map {x => scala.json.ast.unsafe.JArray.apply(x.toArray) }

  /**
    * Generates a valid field name where the first character is alphabetical and the remaining chars
    * are alphanumeric.
    */
  def genFieldName: Gen[String] = Gen.identifier

  def genFields(implicit maxDepth: Depth = defaultMaxDepth, maxWidth: Width = defaultMaxWidth): Gen[(String, scala.json.ast.unsafe.JValue)] = {
    // The Scala compiler has a bug with AnyVal, where it favors implicits in the outer scope
    Gen.zip(genFieldName, genJsValue(maxDepth, maxWidth))
  }

  /**
    * Generates a nested array at the specified depth and width.
    *
    * @param maxDepth see [[defaultMaxDepth]] (cannot be less than 1)
    * @param maxWidth see [[defaultMaxWidth]] (cannot be less than 1)
    */
  def genJsObject(implicit maxDepth: Depth = defaultMaxDepth, maxWidth: Width = defaultMaxWidth): Gen[scala.json.ast.unsafe.JObject] = {
    for {
      fields <- Gen.listOfN(maxWidth, genFields(maxDepth - 1, maxWidth)).map{_.map{case (k,v) => scala.json.ast.unsafe.JField(k,v)}}
    } yield scala.json.ast.unsafe.JObject(fields.toArray)
  }

  // Shrinks for better error output

  implicit val shrinkJsArray: Shrink[scala.json.ast.unsafe.JArray] = Shrink {
    arr =>
      val stream: Stream[scala.json.ast.unsafe.JArray] = shrink(arr.value) map scala.json.ast.unsafe.JArray.apply
      stream
  }

  implicit val shrinkJsObject: Shrink[scala.json.ast.unsafe.JObject] = Shrink {
    obj =>
      val stream: Stream[scala.json.ast.unsafe.JObject] = shrink(obj.value) map { fields => scala.json.ast.unsafe.JObject(fields) }
      stream
  }

  implicit val shrinkJsValue: Shrink[scala.json.ast.unsafe.JValue] = Shrink {
    case array: scala.json.ast.unsafe.JArray => shrink(array)
    case obj: scala.json.ast.unsafe.JObject  => shrink(obj)
    case scala.json.ast.unsafe.JString(str)  => shrink(str) map scala.json.ast.unsafe.JString
    case scala.json.ast.unsafe.JNumber(num)  => shrink(num) map (x => scala.json.ast.unsafe.JNumber(x))
    case scala.json.ast.unsafe.JNull | scala.json.ast.unsafe.JBoolean(_) => Stream.empty[scala.json.ast.unsafe.JValue]
  }

}
