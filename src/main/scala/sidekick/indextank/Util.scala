package sidekick.indextank

import org.apache.thrift.async.AsyncMethodCallback
import akka.actor.ActorSystem
import akka.dispatch.{ExecutionContext, Promise}


object Callback {
  implicit val actorSystem = ActorSystem()
  implicit val executor = ExecutionContext.defaultExecutionContext
  def apply[Result]() =  new AsyncMethodCallback[Result] {
    val future = Promise[Result]()

    def onComplete(result: Result) {
      future.complete(Right(result))
    }

    def onError(exception: Throwable): Unit = {
      future.complete(Left(exception))
    }
  }
}

