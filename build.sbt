name := "tagless-mocks"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies ++=  Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
