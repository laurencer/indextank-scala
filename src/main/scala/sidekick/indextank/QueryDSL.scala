package sidekick.indextank

import sidekick.indextank.QuerySyntaxDSL.QuerySyntaxBuilder

object QueryDSL {
  import QuerySyntaxDSL._
  implicit def string2Query(search: String) : Query = new Query(search)
  implicit def string2QueryBuilder(search: String) : QueryBuilder = new QueryBuilder(search)
  implicit def querySyntaxBuilder2Query(builder: QuerySyntaxBuilder) : Query = new QueryBuilder(builder.build().build()).build()
  implicit def queryBuilder2Query(builder: QueryBuilder) : Query = builder.build()
  implicit def querySyntaxBuilder2QueryBuilder(builder: QuerySyntaxBuilder) = new QueryBuilder(builder.build().build())


  class QueryBuilder(val search: String) {
    val query = Query(search)


    def build() : Query = query
  }
  
}