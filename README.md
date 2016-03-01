# Scala JSON AST

[![Join the chat at https://gitter.im/mdedetrich/scala-json-ast](https://badges.gitter.im/mdedetrich/scala-json-ast.svg)](https://gitter.im/mdedetrich/scala-json-ast?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Two minimal implementations of a [JSON](https://en.wikipedia.org/wiki/JSON) `AST`, one that is designed for
performance and another that is designed for correctness/purity.

## Fast AST
Implementation is in `scala.json.ast.fast`

### Goals
- Uses the best performing datastructure's for high performance/low memory usage in construction of a `JValue`
    - `JArray` stored as an `Array`
    - `JObject` stored as an `Array`
    - `JNumber` stored as a `String`
- Doesn't use `Scala`'s `stdlib` collection's library

## Safe AST
Implementation is in `scala.json.ast.safe`

### Goals
- Fully immutable (all collections/types used are immutable)
- `constant`/`effective constant` lookup time for `JArray`/`JObject`
- Better adherence to the [JSON](https://en.wikipedia.org/wiki/JSON) standard.
    - Number representation for `JNumber` is a `BigDecimal` (http://stackoverflow.com/a/13502497/1519631)
    - `JObject` is an actual `Map[String,JValue]`
    - `JArray` is an `Vector`
- Strictly pure. Library has no side effects/throwing errors (even when constructing various `JValue`'s), and hence we can
guarantee that a `JValue` will always contain a valid structure that can be 
serialized/rendered into [JSON](https://en.wikipedia.org/wiki/JSON). There is one exception, and that is for `scala.json.ast.safe.JNumber` 
in `Scala.js` (see `Scala.js` section for more info)

## Conversion between scala.json.ast.safe and scala.json.ast.fast

Any `scala.json.ast.safe.JValue` implements a conversion to `scala.json.ast.fast.JValue` with a `toFast` method, and vice versa with a
`toSafe` method. These conversion methods have been written to be as fast as possible.

There are some peculiarities when converting between the two AST's. When converting a `scala.json.ast.fast.JNumber` to a 
`scala.json.ast.safe.JNumber`, it is possible for this to fail at runtime (since the internal representation of 
`scala.json.ast.fast.JNumber` is a string). It is up to the caller on how to handle this error (and when), 
a runtime check is deliberately avoided on our end for performance reasons.

Converting from a `scala.json.ast.safe.JObject` to a `scala.json.ast.fast.JObject` will produce 
an `scala.json.ast.fast.JObject` with an undefined ordering for its internal `Array`/`js.Array` representation.
This is because a `Map` has no predefined ordering. If you wish to provide ordering, you will either need
to write your own custom conversion to handle this case. Duplicate keys will also be removed for the same reason,
in an undefined manner

Do note that according to the JSON spec, ordering for JObject is not defined. Also note that `Map` 
disregards ordering for equality, however `Array`/`js.Array` equality takes ordering into account.

## .to[T] Conversion

Both `scala.json.ast.safe.JNumber` and `scala.json.ast.fast.JNumber` provide conversions using a `.to[T]` method. These methods 
provide a default fast implementations for converting between different number types (as well
as stuff like `Char[Array]`). You can provide your own implementations of a `.to[T]` 
conversion by creating an `implicit val` that implements a JNumberConverter, i.e.

```scala
implicit val myNumberConverter = new JNumberConverter[SomeNumberType]{
  def apply(b: BigDecimal): SomeNumberType = ???
}
```

Then you just need to provide this implementation in scope for usage

## Scala.js
Scala json ast also provides support for [Scala.js](https://github.com/scala-js/scala-js). 
There is even a separate `AST` implementation specifically for `Scala.js` with `@JSExport` for the various `JValue` types, 
which means you are able to construct a `JValue` in `Javascript`in the rare cases that you may need to do so. 
Hence there are added constructors for various `JValue` subtypes, i.e. you can pass in a `Javascript` `array` (i.e. `[]`) 
to construct a `JArray`, as well as a constructor for `JObject` that allows you to pass in a standard `Javascript` 
object with `JValue` as keys (i.e. `{}`).

Examples of constructing various `JValue`'s are given below.

```javascript
var jArray = new scala.json.ast.safe.JArray([new JString("test")]);

var jObject = new scala.json.ast.safe.JObject({"someString" : jArray});

var jObjectWithBool = scala.json.ast.safe.JObject({
    "someString" : jArray,
    "someBool" : scala.json.ast.safe.JTrue()
});

var jObjectWithBoolAndNumber = scala.json.ast.safe.JObject({
    "someString" : jArray,
    "someBool" : scala.json.ast.safe.JTrue(),
    "someNumber" : new scala.json.ast.safe.JNumber(324324.324)
});

var jObjectWithBoolAndNumberAndNull = new scala.json.ast.safe.JObject({
    "someString" : jArray,
    "someBool" : scala.json.ast.safe.JTrue(),
    "someNumber" : new scala.json.ast.safe.JNumber(324324.324),
    "nullValue": scala.json.ast.safe.JNull()
});
```