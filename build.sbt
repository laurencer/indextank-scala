name := "indextank-scala"

organization := "io.sidekick"

version := "1.0-SNAPSHOT"

scalaVersion := "2.9.1"

publishTo := Some(Resolver.file("file", new File(Path.userHome.absolutePath + "/src/laurencer/repository")))

resolvers ++= Seq(
  "Akka Repo" at "http://akka.io/repository",
  "laurencer Repo" at "http://laurencer.github.com/repository"
)

libraryDependencies ++= Seq(
  "org.apache.cassandra.deps" % "libthrift" % "0.5.0",
  "io.sidekick" % "indextank-thrift" % "1.0"
)


