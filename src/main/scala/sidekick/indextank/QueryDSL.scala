package sidekick.indextank

import indextank.QuerySyntaxDSL.QuerySyntaxBuilder

object QueryDSL {
  implicit def string2Query(search: String) : Query = new Query(search)
  implicit def string2QueryBuilder(search: String) : QueryBuilder = new QueryBuilder(search)
  implicit def queryBuilder2Query(builder: QueryBuilder) : Query = builder.build()
  implicit def querySyntaxBuilder2QueryBuilder(builder: QuerySyntaxBuilder) = new QueryBuilder(builder.build().build())


  class QueryBuilder(val search: String) {
    val query = Query(search)


    def build() : Query = query
  }
  
}