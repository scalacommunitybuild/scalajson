describe("Testing unsafe.JValue", function() {

  it("construct unsafe.JString", function() {
    expect(new scala.json.ast.unsafe.JString("test")).toBeDefined();
  });

  it("unsafe.JString.toJsAny", function() {
    expect(new scala.json.ast.unsafe.JString("test").toJsAny).toEqual("test");
  });

  it("unsafe.JString.value", function() {
    expect(new scala.json.ast.unsafe.JString("test").value).toEqual("test");
  });

  it("construct unsafe.JSArray", function() {
    expect(new scala.json.ast.unsafe.JArray([])).toBeDefined();
  });

  it("unsafe.JSArray.toJsAny", function() {
    expect(new scala.json.ast.unsafe.JArray([]).toJsAny).toEqual([]);
  });

  it("unsafe.JSArray.value", function() {
    expect(new scala.json.ast.unsafe.JArray([]).value).toEqual([]);
  });

  it("construct unsafe.JNull", function() {
    expect(new scala.json.ast.unsafe.JNull()).toBeDefined();
  });

  it("unsafe.JsNull.toJsAny", function() {
    expect(new scala.json.ast.unsafe.JNull().toJsAny).toEqual(null);
  });

  it("construct Double unsafe.JNumber", function() {
    expect(new scala.json.ast.unsafe.JNumber(343435)).toBeDefined();
  });

  it("unsafe.JNumber.toJsAny", function() {
    expect(new scala.json.ast.unsafe.JNumber(343435).toJsAny).toEqual(343435);
  });

  it("unsafe.JNumber.value", function() {
    expect(new scala.json.ast.unsafe.JNumber(343435).value).toEqual("343435");
  });

  it("construct String unsafe.JNumber", function() {
    expect(new scala.json.ast.unsafe.JNumber("34335325")).toBeDefined();
  });

  it("unsafe.JNumber.toJsAny #2", function() {
    expect(new scala.json.ast.unsafe.JNumber("34335325").toJsAny).toEqual(34335325);
  });

  it("unsafe.JNumber.value #2", function() {
    expect(new scala.json.ast.unsafe.JNumber("34335325").value).toEqual("34335325");
  });

  it("construct big unsafe.JNumber and not throw", function() {
    expect(function () {
      new scala.json.ast.unsafe.JNumber(343435e3952305253256)
    }).not.toThrow();
  });

  it("construct unsafe.JObject", function() {
    expect(new scala.json.ast.unsafe.JObject({})).toBeDefined();
  });

  it("unsafe.JObject.toJsAny", function() {
    expect(new scala.json.ast.unsafe.JObject({}).toJsAny).toEqual({});
  });

  it("construct unsafe.JObject Complex", function() {
    expect(new scala.json.ast.unsafe.JObject({
      test: scala.json.ast.unsafe.JString("test")}
    )).toBeDefined();
  });

  it("unsafe.JObject.toJsAny Complex", function() {
    expect(new scala.json.ast.unsafe.JObject({
      test: scala.json.ast.unsafe.JString("test")}
    ).toJsAny).toEqual({
      test: "test"
    });
  });

  it("construct unsafe.JTrue", function() {
    expect(new scala.json.ast.unsafe.JTrue()).toBeDefined();
  });

  it("unsafe.JTrue.toJsAny", function() {
    expect(new scala.json.ast.unsafe.JTrue().toJsAny).toEqual(true);
  });

  it("construct unsafe.JFalse", function() {
    expect(new scala.json.ast.unsafe.JFalse()).toBeDefined();
  });

  it("unsafe.JFalse.toJsAny", function() {
    expect(new scala.json.ast.unsafe.JFalse().toJsAny).toEqual(false);
  });
});
