name := "tagless-mocks"

organization := "io.tvc"

version := "0.1"

scalaVersion := "2.12.6"

scalacOptions += "-Ypartial-unification"

libraryDependencies ++=  Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.typelevel" %% "cats-core" % "1.2.0"
)
