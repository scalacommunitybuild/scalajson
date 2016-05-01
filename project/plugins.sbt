addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.9")

addSbtPlugin("com.joescii" % "sbt-js-test" % "0.1.0")

libraryDependencies ++= Seq(
  "org.webjars"               % "webjars-locator-core" % "0.30"       % "runtime",
  "org.webjars.bower"         % "jasmine"              % "2.4.1"      % "runtime"
)
