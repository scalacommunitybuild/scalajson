name := "Scala Json AST"

val currentScalaVersion = "2.11.8"

lazy val root = project.in(file(".")).
  aggregate(scalaJsonASTJS, scalaJsonASTJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val scalaJsonAST = crossProject.in(file(".")).
  settings(
    name := "scala-json-ast",
    version := "1.0.0-SNAPSHOT",
    scalaVersion := currentScalaVersion,
    crossScalaVersions := Seq(currentScalaVersion,"2.10.6"),
    scalacOptions ++= Seq(
      "-encoding", "UTF-8",
      "-deprecation", // warning and location for usages of deprecated APIs
      "-feature", // warning and location for usages of features that should be imported explicitly
      "-unchecked", // additional warnings where generated code depends on assumptions
      "-Xlint", // recommended additional warnings
      "-Xcheckinit", // runtime error when a val is not initialized due to trait hierarchies (instead of NPE somewhere else)
      "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
      "-Ywarn-value-discard", // Warn when non-Unit expression results are unused
      "-Ywarn-inaccessible",
      "-Ywarn-dead-code"
    ),
    scalacOptions += {
      scalaVersion.value match {
        case v if v.startsWith("2.10.") => "-target:jvm-1.6"
        case v if v.startsWith("2.11.") => "-target:jvm-1.6"
        case v if v.startsWith("2.12.") => "-target:jvm-1.8"
      }
    }
  ).
  jvmSettings(
    // Add JVM-specific settings here
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    libraryDependencies ++= Seq(
      "com.storm-enroute" %% "scalameter" % "0.7" % Test,
      "org.specs2" %% "specs2-core" % "3.7.2" % Test,
      "org.specs2" %% "specs2-scalacheck" % "3.7.2" % Test,
      "org.scalacheck" %% "scalacheck" % "1.13.0" % Test
    ),
    scalacOptions in Test ++= Seq("-Yrangepos")
  ).
  jsSettings(
    libraryDependencies ++= Seq(
      "org.scalacheck" %%% "scalacheck" % "1.13.0" % Test
    )
    // Add JS-specific settings here
  )

lazy val scalaJsonASTJVM = scalaJsonAST.jvm
lazy val scalaJsonASTJS = scalaJsonAST.js
