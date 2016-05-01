name := "Scala Json AST"

val currentScalaVersion = "2.11.8"
val scala210Version = "2.10.6"
val scalaCheckVersion = "1.13.0"
val specs2Version = "3.7.2"

scalaVersion := currentScalaVersion
crossScalaVersions := Seq(currentScalaVersion,scala210Version)

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
      "org.specs2" %% "specs2-core" % specs2Version % Test,
      "org.specs2" %% "specs2-scalacheck" % specs2Version % Test,
      "org.scalacheck" %% "scalacheck" % scalaCheckVersion % Test
    ),
    scalacOptions in Test ++= Seq("-Yrangepos")
  ).
  jsSettings(
    // Add JS-specific settings here
    libraryDependencies ++= Seq(
      "org.scalacheck" %%% "scalacheck" % scalaCheckVersion % Test,
      "com.lihaoyi" %%% "utest" % "0.4.3" % Test
    ),
    testFrameworks += new TestFramework("utest.runner.Framework"),
    jsTest <<= (jsTest dependsOn (fullOptJS in Compile)),
    jsTestResources := {
      val test = (sourceDirectory in Test).value
      val targetArtifact = (artifactPath in fullOptJS in Compile).value
      ((test / "javascript") ** "**.spec.js").get ++ Seq(targetArtifact.getAbsoluteFile)
    }
  )

lazy val scalaJsonASTJVM = scalaJsonAST.jvm
lazy val scalaJsonASTJS = scalaJsonAST.js.enablePlugins(SbtJsTestPlugin)
