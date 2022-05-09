package au.qut.edu.eresearch.serverlesssearch.index;


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

    @JsonProperty(Constants.Query.TERM_ATTRIBUTE_NAME)
    private Map<String, String> termQuery;

    @Override
    public Query build()  {
        Map.Entry<String, String> termEntry = termQuery.entrySet().stream().findAny().orElseThrow(() -> new RuntimeException("No term specified"));
        return new TermQuery(new Term(termEntry.getKey(), termEntry.getValue()));
    }
}
