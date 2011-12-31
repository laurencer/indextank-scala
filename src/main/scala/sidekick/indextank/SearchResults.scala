package sidekick.indextank

import scala.collection.JavaConversions._

case class SearchResult(
                         id: String,
                         relevance: Long,
                         variables: Map[Int, Int],
                         categories: Map[String, String],
                         fields: Map[String, String]
                         )

/**
 * Idiomatic Scala version of the ResultSet Thrift Class.
 */
case class ResultSet(
                      status: String,
                      matches: Int,
                      documents: List[Map[String, String]],
                      facets: Option[Map[String, Map[String, Int]]] = None,
                      didYouMean: Option[String] = None,
                      categories: Option[List[Map[String, String]]] = None,
                      variables: Option[List[Map[Int, Double]]] = None,
                      scores: Option[List[Double]] = None) {
}

object ResultSet {
  def apply(r: com.flaptor.indextank.rpc.ResultSet): ResultSet =
    ResultSet(
      r.get_status,
      r.get_matches,
      r.get_docs.toList.map(_.toMap),
      if (r.is_set_facets) Some(r.get_facets.toMap.mapValues(_.toMap.map(t => (t._1, t._2.intValue)))) else None,
      if (r.is_set_didyoumean) Some(r.get_didyoumean) else None,
      if (r.is_set_categories()) Some(r.get_categories.toList.map(_.toMap)) else None,
      if (r.is_set_variables) Some(r.get_variables.toList.map(_.toMap.map(t => (t._1.intValue(), t._2.doubleValue)))) else None,
      if (r.is_set_scores) Some(r.get_scores.toList.map(_.doubleValue)) else None
    )
}