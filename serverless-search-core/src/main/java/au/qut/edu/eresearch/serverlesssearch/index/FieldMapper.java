package au.qut.edu.eresearch.serverlesssearch.index;

import au.qut.edu.eresearch.serverlesssearch.service.AllField;
import au.qut.edu.eresearch.serverlesssearch.service.SourceField;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.json.base.JacksonJsonValue;
import com.github.wnameless.json.flattener.JsonFlattener;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;

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


    static Function<String, Stream<IndexableField>> SOURCE_FIELD_MAPPINGS = source ->
            Stream.of(new SourceField(source));

    static Function<Map.Entry<String, Object>, Stream<IndexableField>> FIELD_MAPPINGS = flattenedMapEntry ->
            Stream.of(new TextField(flattenedMapEntry.getKey(), flattenedMapEntry.getValue().toString(), Field.Store.NO),
                    new AllField(flattenedMapEntry.getValue().toString()));

    static Function<Map<String, Object>, Stream<IndexableField>> FIELD_MAPPER = flattenedMap ->
            flattenedMap.entrySet().stream().flatMap(FIELD_MAPPINGS);


    public static Function<Map<String, Object>, Stream<IndexableField>> FIELD_STREAM =

            documentMap -> documentMap == null || documentMap.isEmpty() ? Stream.empty() :
            JSON_NODE.andThen(JACKSON_JSON_VALUE)
            .andThen(jacksonJsonValue -> Stream.concat(
                    FLATTEN_AS_STRING.andThen(SOURCE_FIELD_MAPPINGS).apply(jacksonJsonValue),
                    FLATTEN_AS_MAP.andThen(FIELD_MAPPER).apply(jacksonJsonValue)))
            .apply(documentMap);





}
