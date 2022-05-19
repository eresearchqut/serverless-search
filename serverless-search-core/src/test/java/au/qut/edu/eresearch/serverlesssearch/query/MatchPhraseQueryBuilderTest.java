package au.qut.edu.eresearch.serverlesssearch.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.search.PhraseQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class MatchPhraseQueryBuilderTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    public void coerces() {

        Map<String, Object> queryStringQuery = Map.of("match_phrase", Map.of("title", "The wind AND (rises OR rising)"));
        QueryBuilder queryBuilder = OBJECT_MAPPER.convertValue(queryStringQuery, QueryBuilder.class);
        Assertions.assertTrue(queryBuilder instanceof MatchPhraseQueryBuilder);

    }

    @Test
    public void build() {
        Map<String, Object> queryStringQuery = Map.of("match_phrase", Map.of("title", "Stranger things"));
        QueryBuilder queryBuilder = OBJECT_MAPPER.convertValue(queryStringQuery, QueryBuilder.class);
        Assertions.assertEquals(
                new PhraseQuery("title", "stranger", "things"), queryBuilder.build()
        );
    }

    @Test
    public void slop() {
        Map<String, Object> queryStringQuery = Map.of("match_phrase", Map.of("title", Map.of("query", "Dumb and Dumber", "slop", 1)));
        QueryBuilder queryBuilder = OBJECT_MAPPER.convertValue(queryStringQuery, QueryBuilder.class);
        Assertions.assertEquals(
                new PhraseQuery(1, "title", "dumb", "and", "dumber"), queryBuilder.build()
        );
    }

    @Test
    public void analyser() {
        Map<String, Object> queryStringQuery = Map.of("match_phrase", Map.of("title", Map.of("query", "Low and behold", "analyzer", "stop")));
        QueryBuilder queryBuilder = OBJECT_MAPPER.convertValue(queryStringQuery, QueryBuilder.class);
        Assertions.assertEquals(
                new PhraseQuery("title", "low", "behold"), queryBuilder.build()
        );
    }

}
