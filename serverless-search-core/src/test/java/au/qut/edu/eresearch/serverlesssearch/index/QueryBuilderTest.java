package au.qut.edu.eresearch.serverlesssearch.index;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class QueryBuilderTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    public void queryStringQueryBuilder() {

        Map<String, Object> queryStringQuery = Map.of("query_string", Map.of("query", "The wind AND (rises OR rising)"));
        QueryBuilder queryBuilder = OBJECT_MAPPER.convertValue(queryStringQuery, QueryBuilder.class);
        Assert.assertNotNull(queryBuilder);

    }
}
