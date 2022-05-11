package au.qut.edu.eresearch.serverlesssearch.query;


import au.qut.edu.eresearch.serverlesssearch.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import java.util.Map;

@RegisterForReflection
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class MatchNoneQueryBuilder implements QueryBuilder {

    @JsonProperty(Constants.Query.MATCH_NONE_QUERY_ATTRIBUTE_NAME)
    private Map<String, Object> matchNone;

    @Override
    public Query build() {
        return new BooleanQuery.Builder().build();
    }
}
