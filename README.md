# I want it now!

Add `laurencer.github.com/repository` to your sbt resolvers file, and add
`"io.sidekick" %% "indextank-scala" % "1.0"` to your sbt dependencies.

ie.
    resolvers += "laurencer repo" at "http://laurencer.github.com/repository"
    dependencies += "io.sidekick" %% "indextank-scala" % "1.0"

Be warned that I'll probably change the repository layout in the next few
months (so if something breaks just check back here).

# What is it?

An async scala library for interacting with `indextank-engine`. It returns Akka
futures for each operation and internally uses the Thrift Async client.

Note - you must use `laurencer/indextank-engine` which exposes the Thrift
services using the TFramedTransport (required for async operations).

# What isn't it?

This library does not provide any management functions from
`indextank-service` - so basically none of the automated scaling or deployment.

# What is `indextank-thrift.jar`?

It's the generated thrift classes compiled from `document.thrift` in
`indextank-engine/thrift` directory. Probably in the future I'll include the
thrift file and simply use `gen-java`.
