package au.qut.edu.eresearch.serverlesssearch.index;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class MatchQueryBuilderTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    public void coerces() {

        Map<String, Object> queryStringQuery = Map.of("match", Map.of("title", "The wind AND (rises OR rising)"));
        QueryBuilder queryBuilder = OBJECT_MAPPER.convertValue(queryStringQuery, QueryBuilder.class);
        Assertions.assertTrue(queryBuilder instanceof MatchQueryBuilder);

    }

    @Test
    public void buildSimple() {
        Map<String, Object> queryStringQuery = Map.of("match", Map.of("title", "Stranger things"));
        QueryBuilder queryBuilder = OBJECT_MAPPER.convertValue(queryStringQuery, QueryBuilder.class);
        Assertions.assertEquals(queryBuilder.build(),
                new BooleanQuery.Builder()
                        .add(new TermQuery(new Term("title", "stranger")), BooleanClause.Occur.SHOULD)
                        .add(new TermQuery(new Term("title", "things")), BooleanClause.Occur.SHOULD)
                        .build()
        );
    }

    @Test
    public void buildQuery() {
        Map<String, Object> queryStringQuery = Map.of("match", Map.of("title", Map.of("query", "Dumb and Dumber")));
        QueryBuilder queryBuilder = OBJECT_MAPPER.convertValue(queryStringQuery, QueryBuilder.class);
        Assertions.assertEquals(queryBuilder.build(),
                new BooleanQuery.Builder()
                        .add(new TermQuery(new Term("title", "dumb")), BooleanClause.Occur.SHOULD)
                        .add(new TermQuery(new Term("title", "and")), BooleanClause.Occur.SHOULD)
                        .add(new TermQuery(new Term("title", "dumber")), BooleanClause.Occur.SHOULD)
                        .build()
        );
    }

    @Test
    public void buildQueryWithAnalyser() {
        Map<String, Object> queryStringQuery = Map.of("match", Map.of("title", Map.of("query", "Low and behold", "analyzer", "stop")));
        QueryBuilder queryBuilder = OBJECT_MAPPER.convertValue(queryStringQuery, QueryBuilder.class);
        Assertions.assertEquals(queryBuilder.build(),
                new BooleanQuery.Builder()
                        .add(new TermQuery(new Term("title", "low")), BooleanClause.Occur.SHOULD)
                        .add(new TermQuery(new Term("title", "behold")), BooleanClause.Occur.SHOULD)
                        .build()
        );
    }

}
