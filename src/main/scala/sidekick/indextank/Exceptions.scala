package sidekick.indextank

/**
 * Thrown when a response from the IndexTank server was unexpected/deviated from the API documentation,
 * or could not be processed for any other reason.
 */
case class BadServiceResponse(message: String) extends Throwable(message)

/**
 * The index does not exist.
 */
case class InvalidIndex(name: String, message: Option[String] = None) extends Throwable(name)

/**
 * Operation cancelled.
 */
case class OperationCancelled extends Throwable

/**
 * An argument was incorrect or would not have had the desired effect.
 */
case class BadArgument(name: String, message: String) extends Throwable(name + " - " + message)

/**
 * The operation failed because the service rejected it.
 */
case class InvalidOperation(message:String) extends Throwable(message)