package specs

object Utils {
  def unsafeJValueEquals(left: scala.json.ast.unsafe.JValue, right: scala.json.ast.unsafe.JValue): Boolean = {
    (left, right) match {
      case (l: scala.json.ast.unsafe.JString, r: scala.json.ast.unsafe.JString) =>
        l == r
      case (scala.json.ast.unsafe.JNull,scala.json.ast.unsafe.JNull) =>
        true
      case (l: scala.json.ast.unsafe.JNumber, r: scala.json.ast.unsafe.JNumber) =>
        l == r
      case (l: scala.json.ast.unsafe.JArray, r: scala.json.ast.unsafe.JArray) =>
        val rValue = r.value
        l.value.zipWithIndex.forall{case (value,index) =>
          unsafeJValueEquals(value,rValue(index))
        }
      case (l: scala.json.ast.unsafe.JBoolean, r: scala.json.ast.unsafe.JBoolean) =>
        l.get == r.get
      case (l: scala.json.ast.unsafe.JObject, r: scala.json.ast.unsafe.JObject) =>
        val rAsMap = r.value.map{field => (field.field,field.value)}.toMap
        val lAsMap = l.value.map{field => (field.field,field.value)}.toMap
        rAsMap.forall{case (k,value) =>
          lAsMap.get(k) match {
            case Some(lValue) => unsafeJValueEquals(lValue,value)
            case _ => false
          }
        }

      case _ => false
    }
  }
}
