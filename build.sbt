name := "complex-processes-2025-alexis-philip-thomas"

version := "0.1.0"

scalaVersion := "3.7.1"

val javafxVersion = "24.0.2"


libraryDependencies ++= Seq(
  "org.openjfx" % "javafx-controls" % javafxVersion classifier "linux",
  "org.openjfx" % "javafx-fxml" % javafxVersion classifier "linux",
)

fork := true

javaOptions ++= Seq(
  "--module-path", s"${baseDirectory.value}/lib/javafx-sdk-$javafxVersion/lib",
  "--add-modules", "javafx.controls,javafx.fxml"
)