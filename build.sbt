import Dependencies._

ThisBuild / scalaVersion     := "3.7.1"
ThisBuild / version          := "0.1.0-SNAPSHOT"

val javafxVersion = "24.0.2"

lazy val root = (project in file("."))
  .settings(
    name := "complex-processes-2025-alexis-philip-thomas",
  )

libraryDependencies ++= Seq(
  "org.openjfx" % "javafx-controls" % javafxVersion classifier "win",
  "org.openjfx" % "javafx-controls" % javafxVersion classifier "linux",
  "org.openjfx" % "javafx-controls" % javafxVersion classifier "mac"
)