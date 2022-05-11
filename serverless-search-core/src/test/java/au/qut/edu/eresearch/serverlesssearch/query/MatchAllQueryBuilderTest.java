package au.qut.edu.eresearch.serverlesssearch.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.search.BooleanQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

public class MatchAllQueryBuilderTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    public void coerces() {
        Map<String, Object> queryStringQuery = Map.of("match_all", Collections.EMPTY_MAP);
        QueryBuilder queryBuilder = OBJECT_MAPPER.convertValue(queryStringQuery, QueryBuilder.class);
        Assertions.assertTrue(queryBuilder instanceof MatchAllQueryBuilder);
    }

    @Test
    public void build() {
        Map<String, Object> queryStringQuery = Map.of("match_all", Collections.EMPTY_MAP);
        QueryBuilder queryBuilder = OBJECT_MAPPER.convertValue(queryStringQuery, QueryBuilder.class);
        Assertions.assertEquals(queryBuilder.build(),
                new BooleanQuery.Builder()
                        .build()
        );
    }

    

}
