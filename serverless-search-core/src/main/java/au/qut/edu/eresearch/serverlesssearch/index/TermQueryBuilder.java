package au.qut.edu.eresearch.serverlesssearch.index;


import au.qut.edu.eresearch.serverlesssearch.Constants;
import au.qut.edu.eresearch.serverlesssearch.query.QueryBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.util.Map;

@RegisterForReflection
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class TermQueryBuilder implements QueryBuilder {


    public TermQueryBuilder(String fieldName, String term) {
        this.termQuery = Map.of(fieldName, term);
    }

    @JsonProperty(Constants.Query.TERM_QUERY_ATTRIBUTE_NAME)
    private Map<String, String> termQuery;

    @Override
    public Query build()  {
        Map.Entry<String, String> termEntry = termQuery.entrySet().stream().findAny().orElseThrow(() -> new RuntimeException("No term specified"));
        return new TermQuery(new Term(termEntry.getKey(), termEntry.getValue()));
    }
}
