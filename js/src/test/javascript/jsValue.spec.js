describe("Testing JValue", function() {

  it("construct JString", function() {
    expect(new scala.json.ast.JString("test")).toBeDefined();
  });

  it("JString.toJsAny", function() {
    expect(new scala.json.ast.JString("test").toJsAny).toEqual("test");
  });

  it("JString.value", function() {
    expect(new scala.json.ast.JString("test").value).toEqual("test");
  });
  
  it("construct JSArray", function() {
    expect(new scala.json.ast.JArray([])).toBeDefined();
  });

  it("JSArray.toJsAny", function() {
    expect(new scala.json.ast.JArray([]).toJsAny).toEqual([]);
  });

  it("construct JNull", function() {
    expect(new scala.json.ast.JNull()).toBeDefined();
  });

  it("JsNull.toJsAny", function() {
    expect(new scala.json.ast.JNull().toJsAny).toEqual(null);
  });

  it("construct Double JNumber", function() {
    expect(new scala.json.ast.JNumber(343435)).toBeDefined();
  });

  it("JNumber.toJsAny", function() {
    expect(new scala.json.ast.JNumber(343435).toJsAny).toEqual(343435);
  });

  it("JNumber.value", function() {
    expect(new scala.json.ast.JNumber(343435).value).toEqual("343435");
  });

  it("construct String JNumber", function() {
    expect(new scala.json.ast.JNumber("34335325")).toBeDefined();
  });

  it("JNumber.toJsAny #2", function() {
    expect(new scala.json.ast.JNumber("34335325").toJsAny).toEqual(34335325);
  });

  it("JNumber.value #2", function() {
    expect(new scala.json.ast.JNumber("34335325").value).toEqual("34335325");
  });
  
  it("construct big JNumber and throw", function() {
    expect(function () {
      new scala.json.ast.JNumber(343435e3952305253256)
    }).toThrow();
  });

  it("construct JObject", function() {
    expect(new scala.json.ast.JObject({})).toBeDefined();
  });

  it("JObject.toJsAny", function() {
    expect(new scala.json.ast.JObject({}).toJsAny).toEqual({});
  });
  
  it("construct JObject Complex", function() {
    expect(new scala.json.ast.JObject({
      test: scala.json.ast.JString("test")}
    )).toBeDefined();
  });

  it("JObject.toJsAny Complex", function() {
    expect(new scala.json.ast.JObject({
      test: scala.json.ast.JString("test")}
    ).toJsAny).toEqual({
      test: "test"
    });
  });

  it("construct JTrue", function() {
    expect(new scala.json.ast.JTrue()).toBeDefined();
  });

  it("JTrue.toJsAny", function() {
    expect(new scala.json.ast.JTrue().toJsAny).toEqual(true);
  });

  it("construct JFalse", function() {
    expect(new scala.json.ast.JFalse()).toBeDefined();
  });

  it("JFalse.toJsAny", function() {
    expect(new scala.json.ast.JFalse().toJsAny).toEqual(false);
  });
});
