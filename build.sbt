name := "indextank-scala"

organization := "io.sidekick"

version := "1.0"

scalaVersion := "2.9.1"

resolvers ++= Seq(
  "Akka Repo" at "http://akka.io/repository"
)

libraryDependencies ++= Seq(
  "se.scalablesolutions.akka" % "akka-actor" % "1.2",
  "org.apache.thrift" % "libthrift" % "0.8.0"
)


