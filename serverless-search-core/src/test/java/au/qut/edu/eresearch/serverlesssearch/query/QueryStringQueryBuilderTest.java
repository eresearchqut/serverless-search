package au.qut.edu.eresearch.serverlesssearch.query;

import au.qut.edu.eresearch.serverlesssearch.query.QueryBuilder;
import au.qut.edu.eresearch.serverlesssearch.query.QueryStringQueryBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class QueryStringQueryBuilderTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    public void coerces() {
        Map<String, Object> queryStringQuery = Map.of("query_string", Map.of("query", "The wind AND (rises OR rising)"));
        QueryBuilder queryBuilder = OBJECT_MAPPER.convertValue(queryStringQuery, QueryBuilder.class);
        Assertions.assertTrue(queryBuilder instanceof QueryStringQueryBuilder);
    }

    @Test
    public void build() throws Exception {
        Map<String, Object> queryStringQuery = Map.of("query_string", Map.of("query", "A long kiss goodnight"));
        QueryBuilder queryBuilder = OBJECT_MAPPER.convertValue(queryStringQuery, QueryBuilder.class);
        Assertions.assertEquals(queryBuilder.build(),
                new QueryParser("_all", new StandardAnalyzer()).parse("A long kiss goodnight")
        );
    }


}
