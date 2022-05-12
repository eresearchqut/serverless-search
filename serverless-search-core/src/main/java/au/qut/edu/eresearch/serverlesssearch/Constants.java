package au.qut.edu.eresearch.serverlesssearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Constants {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final class Analysers {

        public static final String STANDARD_ANALYZER_NAME = "standard";

        public static final Supplier<Analyzer> STANDARD_ANALYZER = StandardAnalyzer::new;


        public static final String STOP_ANALYZER_NAME = "stop";


        public static final Supplier<Analyzer> STOP_ANALYZER = () -> new StopAnalyzer(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);

        public static final Supplier<Analyzer> DEFAULT_ANALYZER = STANDARD_ANALYZER;

        public static final Map<String, Supplier<Analyzer>> ANALYZERS = Map.of(
                STANDARD_ANALYZER_NAME, STANDARD_ANALYZER,
                STOP_ANALYZER_NAME, STOP_ANALYZER
        );

        public static final Function<String, Analyzer> ANALYZER = (analyzerName) ->
                ANALYZERS.getOrDefault(Optional.ofNullable(analyzerName).orElse(STANDARD_ANALYZER_NAME), DEFAULT_ANALYZER).get();

    }

    public static final class Fields {
        public static final String ALL_FIELD_NAME = "_all";
        public static final String SOURCE_FIELD_NAME = "_source";
        public static final String ID_FIELD_NAME = "_id";
    }


    public static final class Query {

        public static final int DEFAULT_SIZE = 10;

        public static final String ANALYZER_ATTRIBUTE_NAME = "analyzer";

        public static final String QUERY_ATTRIBUTE_NAME = "query";

        public static final String QUERY_STRING_ATTRIBUTE_NAME = "query_string";

        public static final String DEFAULT_FIELD_ATTRIBUTE_NAME = "default_field";

        public static final String MATCH_QUERY_ATTRIBUTE_NAME = "match";

        public static final String MATCH_ALL_QUERY_ATTRIBUTE_NAME = "match_all";

        public static final String MATCH_NONE_QUERY_ATTRIBUTE_NAME = "match_none";

        public static final String TERM_QUERY_ATTRIBUTE_NAME = "term";

        public static final Function<String, Map<String, Object>> QUERY_STRING_QUERY_MAP =
                (query) -> Map.of(QUERY_STRING_ATTRIBUTE_NAME, Map.of(QUERY_ATTRIBUTE_NAME, query));


        public static final Map<String, Object> MATCH_ALL_QUERY_MAP =
                Map.of(MATCH_ALL_QUERY_ATTRIBUTE_NAME, Collections.EMPTY_MAP);

        public static final Map<String, Object> MATCH_NONE_QUERY_MAP =
                Map.of(MATCH_NONE_QUERY_ATTRIBUTE_NAME, Collections.EMPTY_MAP);

        public static final BiFunction<String, String, Map<String, Object>> TERM_QUERY_MAP =
                (field, term) -> Map.of(TERM_QUERY_ATTRIBUTE_NAME, Map.of(field, term));


        public static final Function<Map<String, Map<String, String>>, Optional<SortField>>
                SORT_FIELD = (sortFieldMap) ->
                sortFieldMap
                        .entrySet()
                        .stream()
                        .findFirst()
                        .map(fieldEntry -> new SortField(fieldEntry.getKey(), SortField.Type.STRING));


        public static final Function<List<Map<String, Map<String, String>>>, List<SortField>>
                SORT_FIELDS = (sortFieldList) -> sortFieldList.stream().sequential().map(SORT_FIELD)
                .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());

        public static final Function<List<Map<String, Map<String, String>>>, Sort> SORT =  (sortFieldList) ->
                Optional.ofNullable(sortFieldList).map(SORT_FIELDS)
                        .map(sortFields -> sortFields.toArray(new SortField[0]))
                        .map(sortFields -> sortFields.length == 0 ? Sort.RELEVANCE : new Sort(sortFields))
                        .orElse(Sort.RELEVANCE);
    }


    public static final class Parsers {

        public static final BiFunction<String, String, QueryParser> PARSER = (defaultField, analyzerName)
                -> new QueryParser(Optional.ofNullable(defaultField).orElse(Fields.ALL_FIELD_NAME), Analysers.ANALYZER.apply(analyzerName));


    }
}
