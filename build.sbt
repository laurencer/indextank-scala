name := "indextank-scala"

organization := "io.sidekick"

version := "1.0"

scalaVersion := "2.9.1"

publishTo := Some(Resolver.file("file", new File(Path.userHome.absolutePath + "/src/laurencer/repository")))

resolvers ++= Seq(
  "Akka Repo" at "http://akka.io/repository"
)

libraryDependencies ++= Seq(
  "se.scalablesolutions.akka" % "akka-actor" % "1.2",
  "org.apache.thrift" % "libthrift" % "0.5.0"
)


