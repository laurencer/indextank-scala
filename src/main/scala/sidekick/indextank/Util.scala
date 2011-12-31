package sidekick.indextank

import org.apache.thrift.async.AsyncMethodCallback
import akka.dispatch.Promise


object Callback {
  def apply[Result]() =  new AsyncMethodCallback[Result] {
    val future = Promise[Result]()

    def onComplete(result: Result) {
      future.completeWithResult(result)
    }

    def onError(exception: Throwable): Unit = {
      future.completeWithException(exception);
    }
  }
}

