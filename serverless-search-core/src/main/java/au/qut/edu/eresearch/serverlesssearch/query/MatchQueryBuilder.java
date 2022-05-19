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


    @Override
    public Query build() {
        QueryConfig matchQuery = QueryMapper.QUERY_CONFIG(this.match);
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

        try (Analyzer analyzer = Constants.Analysers.ANALYZER.apply(matchQuery.getAnalyzerName());
             TokenStream stream = analyzer.tokenStream(matchQuery.getField(), new StringReader(matchQuery.getQuery()))) {
            stream.reset();
            while (stream.incrementToken()) {
                Query termQuery = new TermQuery(new Term(
                        matchQuery.getField(),
                        stream.getAttribute(CharTermAttribute.class).toString()));
                queryBuilder.add(termQuery, BooleanClause.Occur.SHOULD);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return queryBuilder.build();
    }
}
