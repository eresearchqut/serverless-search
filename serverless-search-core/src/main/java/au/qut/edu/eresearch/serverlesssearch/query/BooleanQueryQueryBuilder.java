package au.qut.edu.eresearch.serverlesssearch.query;

import au.qut.edu.eresearch.serverlesssearch.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

@RegisterForReflection
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class BooleanQueryQueryBuilder implements QueryBuilder {

    @JsonProperty(Constants.Query.BOOL_ATTRIBUTE_NAME)
    private BooleanQueryQueryBuilders bool;

    @RegisterForReflection
    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class BooleanQueryQueryBuilders {

        @JsonProperty(Constants.Query.MUST_ATTRIBUTE_NAME)
        private QueryBuilder must;

        @JsonProperty(Constants.Query.MUST_NOT_ATTRIBUTE_NAME)
        private QueryBuilder mustNot;

        @JsonProperty(Constants.Query.SHOULD_ATTRIBUTE_NAME)
        private QueryBuilder should;

        @JsonProperty(Constants.Query.FILTER_ATTRIBUTE_NAME)
        private QueryBuilder filter;

    }

    @Override
    public Query build() {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        if (this.bool.getMust() != null) {
            builder.add(this.bool.getMust().build(), BooleanClause.Occur.MUST);
        }
        if (this.bool.getMustNot() != null) {
            builder.add(this.bool.getMustNot().build(), BooleanClause.Occur.MUST_NOT);
        }
        if (this.bool.getShould() != null) {
            builder.add(this.bool.getShould().build(), BooleanClause.Occur.SHOULD);
        }
        if (this.bool.getFilter() != null) {
            builder.add(this.bool.getFilter().build(), BooleanClause.Occur.FILTER);
        }
        return builder.build();
    }
}
