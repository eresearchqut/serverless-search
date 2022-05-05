package au.qut.edu.eresearch.serverlesssearch.index;

import au.qut.edu.eresearch.serverlesssearch.model.QueryStringQuery;
import lombok.Builder;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@Builder
public class QueryMapper {

    public static BiFunction<QueryParser, String, Query> PARSE_QUERY = (queryParser, query) -> {
        try {
            return queryParser.parse(query);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };


    public static final Analyzer DEFAULT_ANALYSER = new StandardAnalyzer();

    public static final Map<String, Analyzer> ANALYZERS = Map.of(null, DEFAULT_ANALYSER, "standard", DEFAULT_ANALYSER);

    public static final Function<String, Analyzer> ANALYZER = (analyzerName) ->
            ANALYZERS.computeIfAbsent(analyzerName, (key) -> DEFAULT_ANALYSER);

    public static final Function<QueryStringQuery, QueryParser> QUERY_STRING_QUERY_PARSER  = queryStringQuery
            -> new QueryParser(Optional.ofNullable(queryStringQuery.getDefaultField()).orElse(AllField.FIELD_NAME)
            , ANALYZER.apply(queryStringQuery.getAnalyzer()));


    public static final Function<QueryStringQuery, Query> QUERY_STRING_QUERY = queryStringQuery ->
            QUERY_STRING_QUERY_PARSER
                    .andThen(queryParser -> PARSE_QUERY.apply(queryParser, queryStringQuery.getQuery()))
                    .apply(queryStringQuery);
}
