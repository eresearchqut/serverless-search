package au.qut.edu.eresearch.serverlesssearch.index;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.io.StringReader;
import java.util.Map;

@RegisterForReflection
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class MatchQueryBuilder implements QueryBuilder {

    @JsonProperty(Constants.Query.MATCH_QUERY_ATTRIBUTE_NAME)
    private Map<String, Object> match;

    @RegisterForReflection
    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class MatchQuery {

        private String query;

        @JsonProperty(Constants.Query.ANALYZER_ATTRIBUTE_NAME)
        private String analyzerName = Constants.Analysers.STANDARD_ANALYZER_NAME;

    }


    @Override
    public Query build() {

        Map.Entry<String, Object> matchEntry = this.match.entrySet().stream().findAny().orElseThrow(() -> new RuntimeException("No match specified"));
        String fieldName = matchEntry.getKey();
        MatchQuery matchQuery = null;

        if (matchEntry.getValue() instanceof String) {
            matchQuery = new MatchQuery().setQuery((String) matchEntry.getValue());
        } else if (matchEntry.getValue() instanceof Map) {
            matchQuery = Constants.OBJECT_MAPPER.convertValue(matchEntry.getValue(), MatchQuery.class);
        }

        if (matchQuery == null || matchQuery.getQuery() == null) {
            throw new RuntimeException("No query specified");
        }

        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

        try (Analyzer analyzer = Constants.Analysers.ANALYZER.apply(matchQuery.getAnalyzerName());
             TokenStream stream = analyzer.tokenStream(fieldName, new StringReader(matchQuery.getQuery()))) {
            stream.reset();
            while (stream.incrementToken()) {
                Query termQuery = new TermQuery(new Term(
                        fieldName,
                        stream.getAttribute(CharTermAttribute.class).toString()));
                queryBuilder.add(termQuery, BooleanClause.Occur.SHOULD);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return queryBuilder.build();
    }
}
