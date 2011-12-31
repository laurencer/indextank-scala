package sidekick.indextank


import akka.dispatch.Future
import com.flaptor.indextank.rpc.{Suggestor, Indexer, Searcher}
import java.util.HashMap
import org.apache.thrift.transport._
import scala.Int
import scala.collection.JavaConversions._
import org.apache.thrift.async.TAsyncClientManager
import org.apache.thrift.protocol.{TProtocolFactory, TProtocol, TBinaryProtocol}

class SearchService(val indexHost: String, val searchHost: String, val suggestionHost: String) {
  /**
   * Helper method to create a TTransport by extracting hostname and port from host string.
   */
  def buildTransport(hosts: String): TNonblockingTransport = {

    val parts = searchHost.split(":")
    val host = parts(0)
    val port = parts(1).toInt

    new TNonblockingSocket(host, port.intValue)
  }

  /**
   * Helper method to generate a protocol factory that uses the given transport.
   */
  def buildProtocolFactory(transport: TTransport) = new TProtocolFactory() {
    def getProtocol(transport: TTransport): TProtocol = new TBinaryProtocol(transport)
  }

  /**
   * Required for async thrift operations.
   */
  val asyncManager = new TAsyncClientManager()

  /**
   * Transports used for operations.
   */
  val searchTransport = buildTransport(searchHost)
  val suggestionTransport = buildTransport(suggestionHost)
  val indexTransport = buildTransport(indexHost)

  /**
   * Services used for operations.
   */
  val searchServiceFactory = new Searcher.AsyncClient.Factory(asyncManager, buildProtocolFactory(searchTransport))
  val indexServiceFactory = new Indexer.AsyncClient.Factory(asyncManager, buildProtocolFactory(indexTransport))
  val suggestionServiceFactory = new Suggestor.AsyncClient.Factory(asyncManager, buildProtocolFactory(suggestionTransport))

  def searchService() = searchServiceFactory.getAsyncClient(searchTransport)
  def indexService() = indexServiceFactory.getAsyncClient(searchTransport)
  def suggestionService() = suggestionServiceFactory.getAsyncClient(searchTransport)

  /**
   * Helper function to convert a future result to a Unit result type.
   */
  def toUnit[T]: (T => Unit) = (t: T) => {}

  implicit def document2IndextankDocument(doc: Document): com.flaptor.indextank.rpc.Document = {
    new com.flaptor.indextank.rpc.Document().set_fields(doc.fields)
  }

  def createDocument(document: Document): Future[Unit] = {
    val timestampBoost = 0
    val boosts = new HashMap[java.lang.Integer, java.lang.Double]()
    val callback = Callback[Indexer.AsyncClient.addDoc_call]()
    indexService.addDoc(document.id, document, timestampBoost, boosts, callback)
    callback.future.map(toUnit)
  }

  def deleteDocument(documentId: String): Future[Unit] = {
    val callback = Callback[Indexer.AsyncClient.delDoc_call]()
    indexService.delDoc(documentId, callback)
    callback.future.map(toUnit)
  }

  def updateBoosts(documentId: String, boosts: Map[Int, Double]): Future[Unit] = {
    val callback = Callback[Indexer.AsyncClient.updateBoost_call]()
    indexService.updateBoost(documentId, boosts.map(t => (t._1.asInstanceOf[java.lang.Integer], t._2.asInstanceOf[java.lang.Double])), callback)
    callback.future.map(toUnit)
  }

  def updateCategories(documentId: String, categories: Map[String, String]): Future[Unit] = {
    val callback = Callback[Indexer.AsyncClient.updateCategories_call]()
    indexService.updateCategories(documentId, categories, callback)
    callback.future.map(toUnit)
  }

  def promote(documentId: String, query: String): Future[Unit] = {
    val callback = Callback[Indexer.AsyncClient.promoteResult_call]()
    indexService.promoteResult(documentId, query, callback)
    callback.future.map(toUnit)
  }

  def search(q: Query) = {
    import scala.collection.JavaConversions._
    val callback = Callback[Searcher.AsyncClient.search_call]()
    searchService.search(q.query, q.start, q.resultLength, q.scoringFunction,
      q.queryVariables.map(t => (t._1.asInstanceOf[java.lang.Integer], t._2.asInstanceOf[java.lang.Double])),
      q.categoryFilters.map(_.toIndexTank),
      q.variableRangeFilters.map(_.toIndexTank),
      q.functionRangeFilters.map(_.toIndexTank), q.extraParameters(), callback)
    callback.future.map(c => c.getResult)
  }

  def autocomplete(query: String, field: String = ""): Future[AutocompleteResults] = {
    import scala.collection.JavaConversions._
    val callback = Callback[Suggestor.AsyncClient.complete_call]()
    suggestionService.complete(query, field, callback)
    callback.future.map(c => c.getResult).map(results => AutocompleteResults(query, results.toList))
  }
}