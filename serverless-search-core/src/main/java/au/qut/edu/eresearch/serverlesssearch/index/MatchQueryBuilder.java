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

    @JsonProperty(Constants.Query.MATCH_ATTRIBUTE_NAME)
    private Map<String, Object> matchQuery;

    @Override
    public Query build() {
        Map.Entry<String, Object> matchEntry = matchQuery.entrySet().stream().findAny().orElseThrow(() -> new RuntimeException("No match specified"));
        String query = null;
        String analyzerName = Constants.Analysers.STANDARD_ANALYZER_NAME;
        String fieldName = matchEntry.getKey();
        if (matchEntry.getValue() instanceof String) {
            query = (String) matchEntry.getValue();
        } else if (matchEntry.getValue() instanceof Map) {
            Map<String, Object> matchConfig = (Map<String, Object>) matchEntry.getValue();
            if (!matchConfig.containsKey(Constants.Query.QUERY_ATTRIBUTE_NAME)) {
                throw new RuntimeException("No query specified");
            }
            query = matchConfig.get(Constants.Query.QUERY_ATTRIBUTE_NAME).toString();
            analyzerName = matchConfig.getOrDefault(Constants.Query.ANALYZER_ATTRIBUTE_NAME, Constants.Analysers.STANDARD_ANALYZER_NAME).toString();
        } else {
            throw new RuntimeException("No query specified");
        }

        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        Analyzer analyzer = Constants.Analysers.ANALYZER.apply(analyzerName);
        try (TokenStream stream = analyzer.tokenStream(fieldName, new StringReader(query))) {
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
