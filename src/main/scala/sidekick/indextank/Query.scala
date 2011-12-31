package sidekick.indextank


case class CategoryFilter(name: String, value: String) {
  def toIndexTank(): com.flaptor.indextank.rpc.CategoryFilter = {
    new com.flaptor.indextank.rpc.CategoryFilter(name, value)
  }
}

case class RangeFilter(key: Int, floor: Option[Double] = None, ceil: Option[Double] = None) {
  def toIndexTank(): com.flaptor.indextank.rpc.RangeFilter = {
    val filter = new com.flaptor.indextank.rpc.RangeFilter()
    filter.set_key(key)
    floor match {
      case Some(value) => {
        filter.set_floor(value).set_no_floor(false)
      }
      case None => {
        filter.set_no_floor(true)
      }
    }

    ceil match {
      case Some(value) => {
        filter.set_ceil(value).set_no_ceil(false)
      }
      case None => {
        filter.set_no_ceil(true)
      }
    }

    filter
  }
}


case class Query(
                  query: String,
                  var start: Int = 0,
                  var resultLength: Int = 10,
                  var scoringFunction: Int = 0,
                  var queryVariables: Map[Int, Double] = Map(),
                  var categoryFilters: List[CategoryFilter] = List(),
                  var variableRangeFilters: List[RangeFilter] = List(),
                  var functionRangeFilters: List[RangeFilter] = List(),
                  var fetchVariables: Boolean = false,
                  var fetchCategories: Boolean = false) {
  def extraParameters(): Map[String, String] = {
    val map = scala.collection.mutable.Map[String, String]()
    if (fetchVariables) map("fetchVariables") = "*"
    if (fetchCategories) map("fetchCategories") = "*"
    map.toMap
  }

}