package au.qut.edu.eresearch.serverlesssearch.query;

import au.qut.edu.eresearch.serverlesssearch.index.TermQueryBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class BooleanQueryBuilderTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    public void coerces() {
        Map<String, Object> booleanMustQuery = Map.of("bool", Map.of("must", Map.of("query_string", Map.of("query", "The wind AND (rises OR rising)"))));
        QueryBuilder queryBuilder = OBJECT_MAPPER.convertValue(booleanMustQuery, QueryBuilder.class);
        Assertions.assertTrue(queryBuilder instanceof BooleanQueryQueryBuilder);
    }

    @Test
    public void buildMustQuery() throws Exception {
        Map<String, Object> booleanMustQuery = Map.of("bool", Map.of("must", Map.of("query_string", Map.of("query", "The wind AND (rises OR rising)"))));
        QueryBuilder queryBuilder = OBJECT_MAPPER.convertValue(booleanMustQuery, QueryBuilder.class);
        Assertions.assertEquals(
                queryBuilder.build(),
                new BooleanQuery.Builder().add(new QueryParser("_all", new StandardAnalyzer()).parse("The wind AND (rises OR rising)"), BooleanClause.Occur.MUST).build()
        );
    }


    @Test
    public void buildMustNotTerm() {
        Map<String, Object> booleanMustQuery = Map.of("bool", Map.of("must_not", Map.of("term", Map.of("item","frankincense"))));
        QueryBuilder queryBuilder = OBJECT_MAPPER.convertValue(booleanMustQuery, QueryBuilder.class);
        Assertions.assertEquals(
                queryBuilder.build(),
                new BooleanQuery.Builder().add(new TermQueryBuilder("item", "frankincense").build(), BooleanClause.Occur.MUST_NOT).build()
        );
    }

    @Test
    public void buildFilterMatch() {
        Map<String, Object> booleanMustQuery = Map.of("bool", Map.of("filter", Map.of("match", Map.of("song", "under-the-bridge"))));
        QueryBuilder queryBuilder = OBJECT_MAPPER.convertValue(booleanMustQuery, QueryBuilder.class);
        Assertions.assertEquals(
                queryBuilder.build(),
                new BooleanQuery.Builder().add(new BooleanQuery.Builder()
                        .add(new TermQuery(new Term("song", "under")), BooleanClause.Occur.SHOULD)
                        .add(new TermQuery(new Term("song", "the")), BooleanClause.Occur.SHOULD)
                        .add(new TermQuery(new Term("song", "bridge")), BooleanClause.Occur.SHOULD)
                        .build(), BooleanClause.Occur.FILTER).build()
        );
    }

    @Test
    public void buildShouldMatchNone() {
        Map<String, Object> booleanMustQuery = Map.of("bool", Map.of("should", Map.of("match_none", Map.of())));
        QueryBuilder queryBuilder = OBJECT_MAPPER.convertValue(booleanMustQuery, QueryBuilder.class);
        Assertions.assertEquals(
                queryBuilder.build(),
                new BooleanQuery.Builder().add( new BooleanQuery.Builder().build(), BooleanClause.Occur.SHOULD).build()
        );
    }


    @Test
    public void buildMustBoolShouldQuery() throws Exception {
        Map<String, Object> booleanMustQuery = Map.of("bool", Map.of("must", Map.of("bool", Map.of("should", Map.of("query_string", Map.of("query", "The wind AND (rises OR rising)"))))));
        QueryBuilder queryBuilder = OBJECT_MAPPER.convertValue(booleanMustQuery, QueryBuilder.class);
        Assertions.assertEquals(
                queryBuilder.build(),
                new BooleanQuery.Builder().add(
                new BooleanQuery.Builder().add(new QueryParser("_all", new StandardAnalyzer()).parse("The wind AND (rises OR rising)"), BooleanClause.Occur.SHOULD).build(), BooleanClause.Occur.MUST).build()
        );
    }


}
