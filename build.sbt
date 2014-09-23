name := """numa08.pvs"""

version := "1.0"

scalaVersion := "2.11.1"

// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.6" % "test"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.3.3"

// analyzers
libraryDependencies ++= Seq(
  "com.google.apis" % "google-api-services-analytic" % "v3-rev100-1.19.0"
)

// reporters
libraryDependencies ++= Seq(
  "org.twitter4j" % "twitter4j" % "4.0.2"
)

// configure
libraryDependencies ++= Seq(
  "com.twitter" % "util-eval_2.10" % "6.20.0"
)