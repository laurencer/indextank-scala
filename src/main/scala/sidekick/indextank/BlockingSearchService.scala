package sidekick.indextank


import akka.dispatch.Future
import com.flaptor.indextank.rpc.{Suggestor, Indexer, Searcher}
import java.util.HashMap
import org.apache.thrift.transport._
import scala.Int
import scala.collection.JavaConversions._
import org.apache.thrift.async.TAsyncClientManager
import org.apache.thrift.protocol.{TProtocolFactory, TProtocol, TBinaryProtocol}

/**
 * Synchronous Indextank API.
 */
class BlockingSearchService(val indexHost: String, val searchHost: String, val suggestionHost: String) {
  /**
   * Helper method to create a TTransport by extracting hostname and port from host string.
   */
  def buildTransport(hosts: String): TTransport = {

    val parts = hosts.split(":")
    val host = parts(0)
    val port = parts(1).toInt
    
    println("creating search service for %s : %s" format (host, port.intValue.toString))
    new TFramedTransport(new TSocket(host, port.intValue))
  }

  /**
   * Helper method to generate a protocol factory that uses the given transport.
   */
  def buildProtocol(transport: TTransport) = new TBinaryProtocol(transport)

  /**
   * Transports used for operations.
   */
  def indexTransport = {
    val t = buildTransport(indexHost)
    t.open()
    t
  }
  def searchTransport = {
    val t = buildTransport(searchHost)
    t.open()
    t
  }
  def suggestionTransport = {
    val t = buildTransport(suggestionHost)
    t.open()
    t
  }
  /**
   * Services used for operations.
   */
  val indexServiceFactory = new Indexer.Client.Factory()
  val searchServiceFactory = new Searcher.Client.Factory()
  val suggestionServiceFactory = new Suggestor.Client.Factory()

  def indexService() = new Indexer.Client((buildProtocol(indexTransport)))
  def searchService() = searchServiceFactory.getClient(buildProtocol(searchTransport))
  def suggestionService() = suggestionServiceFactory.getClient(buildProtocol(suggestionTransport))

  /**
   * Helper function to convert a future result to a Unit result type.
   */
  def toUnit[T]: (T => Unit) = (t: T) => {}

  implicit def document2IndextankDocument(doc: Document): com.flaptor.indextank.rpc.Document = {
    new com.flaptor.indextank.rpc.Document().set_fields(doc.fields)
  }

  def createDocument(document: Document): Unit = {
    val timestampBoost = 0
    val boosts = new HashMap[java.lang.Integer, java.lang.Double]()
    indexService.addDoc(document.id, document, timestampBoost, boosts)
  }

  def deleteDocument(documentId: String): Unit = {
    indexService.delDoc(documentId)
  }

  def updateBoosts(documentId: String, boosts: Map[Int, Double]): Unit = {
    indexService.updateBoost(documentId, boosts.map(t => (t._1.asInstanceOf[java.lang.Integer], t._2.asInstanceOf[java.lang.Double])))
  }

  def updateCategories(documentId: String, categories: Map[String, String]): Unit = {
    indexService.updateCategories(documentId, categories)
  }

  def promote(documentId: String, query: String): Unit = {
    indexService.promoteResult(documentId, query)
  }

  def search(q: Query) : ResultSet = {
    import scala.collection.JavaConversions._
    ResultSet(searchService.search(q.query, q.start, q.resultLength, q.scoringFunction,
      q.queryVariables.map(t => (t._1.asInstanceOf[java.lang.Integer], t._2.asInstanceOf[java.lang.Double])),
      q.categoryFilters.map(_.toIndexTank),
      q.variableRangeFilters.map(_.toIndexTank),
      q.functionRangeFilters.map(_.toIndexTank), q.extraParameters()))
  }

  def autocomplete(query: String, field: String = ""): AutocompleteResults = {
    import scala.collection.JavaConversions._
    AutocompleteResults(query, suggestionService.complete(query, field).toList)
  }
}