package au.qut.edu.eresearch.serverlesssearch.index;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.json.base.JacksonJsonValue;
import com.github.wnameless.json.flattener.JsonFlattener;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexableField;

import java.math.BigInteger;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class FieldMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Function<Map<String, Object>, JsonNode> JSON_NODE = documentMap ->
            objectMapper.convertValue(documentMap, JsonNode.class);

    private static final Function<JsonNode, JacksonJsonValue> JACKSON_JSON_VALUE = JacksonJsonValue::new;

    private static final Function<JacksonJsonValue, String> FLATTEN_AS_STRING = JsonFlattener::flatten;

    private static final Function<JacksonJsonValue, Map<String, Object>> FLATTEN_AS_MAP = JsonFlattener::flattenAsMap;


    static Function<String, Stream<IndexableField>> MAP_SOURCE = source ->
            Stream.of(new SourceField(source));

    static IndexableField mapField(String key, Object value) {
        if (value instanceof Integer) {
            return new IntPoint(key, (Integer) value);
        }
        if (value instanceof Long) {
            return new LongPoint(key, (Long) value);
        }
        if (value instanceof BigInteger) {
            return new BigIntegerPoint(key, (BigInteger) value);
        }
        if (value instanceof Double) {
            return new DoublePoint(key, (Double) value);
        }
        return new TextField(key, value.toString(), Field.Store.NO);
    }


    static Function<Map.Entry<String, Object>, Stream<IndexableField>> MAP_FIELD_AND_ALL = flattenedMapEntry ->
            Stream.of(mapField(flattenedMapEntry.getKey(), flattenedMapEntry.getValue()),
                    new AllField(flattenedMapEntry.getValue().toString()));

    static Function<Map<String, Object>, Stream<IndexableField>> MAP_FIELDS = flattenedMap ->
            flattenedMap.entrySet().stream().flatMap(MAP_FIELD_AND_ALL);


    public static Function<Map<String, Object>, Stream<IndexableField>> FIELDS =
            documentMap -> documentMap == null || documentMap.isEmpty() ? Stream.empty() :
                    JSON_NODE.andThen(JACKSON_JSON_VALUE)
                            .andThen(jacksonJsonValue -> Stream.concat(
                                    FLATTEN_AS_STRING.andThen(MAP_SOURCE).apply(jacksonJsonValue),
                                    FLATTEN_AS_MAP.andThen(MAP_FIELDS).apply(jacksonJsonValue)))
                            .apply(documentMap);


}
