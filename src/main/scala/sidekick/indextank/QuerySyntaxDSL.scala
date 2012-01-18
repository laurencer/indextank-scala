package sidekick.indextank

/**
 * DSL to build the query search syntax (note that this is distinct from the actual query). The query
 * contains additional information such as how many results to return and the fields to return.
 *
 * The Query Syntax refers to the actual search string - ie. 'potatoes AND country:ireland'.
 */
object QuerySyntaxDSL {

  case class QueryTerm (
    term: String,
    field: Option[String],
    weight: Option[Int]
  ) extends QuerySyntax {
    def build() : String = {
      val builder = new StringBuilder

      for (fieldName <- field) {
        builder.append(fieldName)
        builder.append(":")
      }

      if (term.trim().indexOf(" ") > 0) {
        builder.append("\"")
        builder.append(term.trim())
        builder.append("\"")
      } else {
        builder.append(term.trim())
      }

      for (weighting <- weight) {
        builder.append("^")
        builder.append(weighting)
      }

      builder.toString()
    }
  }

  case class Composition(
    first: QuerySyntax,
    second: QuerySyntax,
    operator: String
  ) extends QuerySyntax {
    def build() : String = {
      "(" + first.build() + " " + operator + " " + second.build() + ")"
    }
  }

  implicit def string2QueryElementBuilder(term: String) = new QueryTermBuilder(term)
  implicit def queryElementBuilder2CompositionBuilder(first: QueryTermBuilder) = new CompositionBuilder(first)
  implicit def querySyntaxBuilder2string(builder: QuerySyntaxBuilder) : String = builder.build().build()

  case class QuerySyntaxException(message: String) extends Throwable

  trait QuerySyntaxBuilder {
    def build() : QuerySyntax
  }

  class CompositionBuilder(val first: QuerySyntaxBuilder) extends QuerySyntaxBuilder {
    var second: QuerySyntaxBuilder = null
    var term: String = null

    def and(second: QuerySyntaxBuilder) : CompositionBuilder = {
      term = "AND"
      this.second = second
      return this
    }
    def or(second: QuerySyntaxBuilder) : CompositionBuilder = {
      term = "OR"
      this.second = second
      return this
    }
    def not(second: QuerySyntaxBuilder) : CompositionBuilder = {
      term = "NOT"
      this.second = second
      return this
    }

    def and(second: QueryTermBuilder) : QueryTermBuilder = {
      term = "AND"
      this.second = second
      second.parent = Some(this)
      return second
    }
    def or(second: QueryTermBuilder) : QueryTermBuilder = {
      term = "OR"
      this.second = second
      second.parent = Some(this)
      return second
    }
    def not(second: QueryTermBuilder) : QueryTermBuilder = {
      term = "NOT"
      this.second = second
      second.parent = Some(this)
      return second
    }

    def build() : Composition = {
      return Composition(first.build(), second.build(), term)
    }

    def build(secondElement: QueryTerm) : Composition = {
      return Composition(first.build(), secondElement, term)
    }
  }

  class QueryTermBuilder(val term: String) extends QuerySyntaxBuilder {
    var field : Option[String] = None
    var weight : Option[Int] = None
    var parent: Option[CompositionBuilder] = None

    def in(field: String) : QueryTermBuilder = {
      if (field.trim().indexOf(" ") > 0) throw new QuerySyntaxException("field names cannot contain spaces")
      this.field = Some(field.trim())
      return this
    }

    def weighted(weight: Int) : QueryTermBuilder = {
      this.weight = Some(weight)
      return this
    }

    def build() : QuerySyntax = {
      val el = QueryTerm(term, field, weight)
      return parent match {
        case Some(composition) => composition.build(el)
        case None => el
      }
    }

  }
}
