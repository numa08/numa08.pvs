name := """numa08.pvs"""

version := "1.0"

scalaVersion := "2.11.1"

// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.6" % "test"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.3.3"

// analyzers
libraryDependencies ++= Seq(
  "com.google.api-client" % "google-api-client" % "1.18.0-rc"
)

// reporters
