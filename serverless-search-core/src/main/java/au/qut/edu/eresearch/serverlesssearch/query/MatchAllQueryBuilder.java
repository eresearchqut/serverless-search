package au.qut.edu.eresearch.serverlesssearch.query;


import au.qut.edu.eresearch.serverlesssearch.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

import java.util.Map;

@RegisterForReflection
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class MatchAllQueryBuilder implements QueryBuilder {

    @JsonProperty(Constants.Query.MATCH_ALL_QUERY_ATTRIBUTE_NAME)
    private Map<String, Object> matchAll;

    @Override
    public Query build() {
        return new MatchAllDocsQuery();
    }
}
