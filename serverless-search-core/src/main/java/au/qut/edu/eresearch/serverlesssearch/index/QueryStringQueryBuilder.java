package au.qut.edu.eresearch.serverlesssearch.index;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

@RegisterForReflection
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class QueryStringQueryBuilder implements QueryBuilder {

    public QueryStringQueryBuilder(String query) {
        this.queryStringQuery = new QueryStringQuery().setQuery(query);
    }


    @JsonProperty(Constants.Query.QUERY_STRING_ATTRIBUTE_NAME)
    private QueryStringQuery queryStringQuery;

    @RegisterForReflection
    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class QueryStringQuery {

        private String query;

        @JsonProperty(Constants.Query.DEFAULT_FIELD_ATTRIBUTE_NAME)
        private String defaultField;

        private String analyzer;

    }

    @Override
    public Query build() {
        QueryParser queryParser = Constants.Parsers.PARSER.apply(this.queryStringQuery.getDefaultField(), this.queryStringQuery.getAnalyzer());
        try {
            return queryParser.parse(this.queryStringQuery.getQuery());
        } catch (ParseException pe) {
            throw new RuntimeException(pe);
        }
    }
}
