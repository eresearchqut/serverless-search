package au.qut.edu.eresearch.serverlesssearch.query;

import au.qut.edu.eresearch.serverlesssearch.Constants;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.search.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class QueryMapper {


    public static final Function<Map<String, Object>, QueryBuilder> QUERY_BUILDER = query ->
            Constants.OBJECT_MAPPER.convertValue(query, QueryBuilder.class);

    public static final Function<Map<String, Object>, Query> QUERY = query ->
            QUERY_BUILDER.apply(query).build();

    public static final Function<String, Map<String, Object>> QUERY_STRING_QUERY_MAP =
            (query) -> Map.of(Constants.Query.QUERY_STRING_ATTRIBUTE_NAME, Map.of(Constants.Query.QUERY_ATTRIBUTE_NAME, query));


    public static final Map<String, Object> MATCH_ALL_QUERY_MAP =
            Map.of(Constants.Query.MATCH_ALL_QUERY_ATTRIBUTE_NAME, Collections.EMPTY_MAP);

    public static final Map<String, Object> MATCH_NONE_QUERY_MAP =
            Map.of(Constants.Query.MATCH_NONE_QUERY_ATTRIBUTE_NAME, Collections.EMPTY_MAP);

    public static final BiFunction<String, String, Map<String, Object>> TERM_QUERY_MAP =
            (field, term) -> Map.of(Constants.Query.TERM_QUERY_ATTRIBUTE_NAME, Map.of(field, term));


    static Function<Map<String, String>, Boolean> SORT_ORDER_DESC = fieldConfig ->
            Optional.ofNullable(fieldConfig.get(Constants.Query.ORDER_ATTRIBUTE_NAME))
                    .map(order -> Constants.Query.ORDER_DESC.equalsIgnoreCase(order))
                    .orElse(Boolean.FALSE);




    static BiFunction<FieldInfo, Boolean, SortField> SORT_FIELD = (fieldInfo, reverse) -> {
        if (DocValuesType.SORTED_NUMERIC.equals(fieldInfo.getDocValuesType())) {
            return new SortedNumericSortField(fieldInfo.name, SortField.Type.LONG, Optional.ofNullable(reverse).orElse(Boolean.FALSE));
        }
        return new SortField(fieldInfo.name, SortField.Type.STRING, Optional.ofNullable(reverse).orElse(Boolean.FALSE));
    };

    public static final BiFunction<Map<String, FieldInfo>, List<Map<String, Map<String, String>>>, List<SortField>>
            SORT_FIELDS = (fieldInfos, sortFieldList) -> sortFieldList.stream().sequential()
            .flatMap(sortFieldConfig -> sortFieldConfig.entrySet().stream())
            .map(fieldConfig ->
                    Optional.ofNullable(fieldInfos.get(fieldConfig.getKey()))
                            .map(fieldInfo -> SORT_FIELD.apply(fieldInfo, SORT_ORDER_DESC.apply(fieldConfig.getValue()))))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

    public static final BiFunction<Map<String, FieldInfo>, List<Map<String, Map<String, String>>>, Sort> SORT = (fieldInfos, sortFieldList) ->
            Optional.ofNullable(sortFieldList)
                    .map(sortFieldListPresent -> SORT_FIELDS.apply(fieldInfos, sortFieldListPresent))
                    .map(sortFields -> sortFields.toArray(new SortField[0]))
                    .map(sortFields -> sortFields.length == 0 ? Sort.RELEVANCE : new Sort(sortFields))
                    .orElse(Sort.RELEVANCE);


    public static final Function<IndexSearcher, Map<String, FieldInfo>> FIELD_INFO = searcher -> searcher.getIndexReader()
            .getContext()
            .leaves()
            .stream()
            .map(lrc -> lrc.reader().getFieldInfos())
            .flatMap(fieldInfos -> StreamSupport.stream(
                    fieldInfos.spliterator(), false))
            .collect(Collectors.toMap(fieldInfo -> fieldInfo.name, Function.identity()));

}
