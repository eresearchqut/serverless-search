package au.qut.edu.eresearch.serverlesssearch.query;


import au.qut.edu.eresearch.serverlesssearch.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

import java.io.StringReader;
import java.util.Map;

@RegisterForReflection
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class MatchPhraseQueryBuilder implements QueryBuilder {

    @JsonProperty(Constants.Query.MATCH_PHRASE_QUERY_ATTRIBUTE_NAME)
    private Map<String, Object> matchPhrase;


    @Override
    public Query build() {
        QueryConfig queryConfig = QueryMapper.QUERY_CONFIG(this.matchPhrase);
        PhraseQuery.Builder queryBuilder = new PhraseQuery.Builder();
        try (Analyzer analyzer = Constants.Analysers.ANALYZER.apply(queryConfig.getAnalyzerName());
             TokenStream stream = analyzer.tokenStream(queryConfig.getField(), new StringReader(queryConfig.getQuery()))) {
            stream.reset();
            while (stream.incrementToken()) {
                queryBuilder.add(new Term(
                        queryConfig.getField(),
                        stream.getAttribute(CharTermAttribute.class).toString()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        queryBuilder.setSlop(queryConfig.getSlop());
        return queryBuilder.build();
    }
}
