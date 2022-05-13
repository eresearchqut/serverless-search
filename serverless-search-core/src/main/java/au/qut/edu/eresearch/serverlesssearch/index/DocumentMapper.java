package au.qut.edu.eresearch.serverlesssearch.index;

import au.qut.edu.eresearch.serverlesssearch.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.wnameless.json.base.JacksonJsonValue;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.github.wnameless.json.unflattener.JsonUnflattener;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;

import java.math.BigInteger;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class DocumentMapper {


    private static final Function<Map<String, Object>, JsonNode> JSON_NODE = documentMap ->
            Constants.OBJECT_MAPPER.convertValue(documentMap, JsonNode.class);

    private static final Function<JsonNode, JacksonJsonValue> JACKSON_JSON_VALUE = JacksonJsonValue::new;

    private static final Function<JacksonJsonValue, String> FLATTEN_AS_STRING = JsonFlattener::flatten;

    private static final Function<JacksonJsonValue, Map<String, Object>> FLATTEN_AS_MAP = JsonFlattener::flattenAsMap;


    static Function<String, Stream<IndexableField>> MAP_SOURCE = source ->
            Stream.of(new SourceField(source));



    static Field docValuesField(String field, Object value) {
        if (value instanceof Integer) {
            return new SortedNumericDocValuesField(field, (Integer) value);
        }
        if (value instanceof Long) {
            return new SortedNumericDocValuesField(field, (Long) value);
        }
        if (value instanceof BigInteger) {
            return new SortedNumericDocValuesField(field, ((BigInteger) value).longValue());
        }
        if (value instanceof Double) {
            return new SortedNumericDocValuesField(field, ((Double) value).longValue());
        }
        return new SortedDocValuesField(field, new BytesRef(value.toString()));
    }


    static Field indexField(String field, Object value) {
        if (value instanceof Integer) {
            return new IntPoint(field, (Integer) value);
        }
        if (value instanceof Long) {
            return new LongPoint(field, (Long) value);
        }
        if (value instanceof BigInteger) {
            return new BigIntegerPoint(field, (BigInteger) value);
        }
        if (value instanceof Double) {
            return new DoublePoint(field, (Double) value);
        }
        return new TextField(field, value.toString(), Field.Store.NO);
    }


    static Function<Map.Entry<String, Object>, Stream<IndexableField>> MAP_FIELD_AND_ALL = flattenedMapEntry ->
            Stream.of(indexField(flattenedMapEntry.getKey(), flattenedMapEntry.getValue()),
                    docValuesField(flattenedMapEntry.getKey(), flattenedMapEntry.getValue()),
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




    public static final Function<Document, Map<String, Object>> GET_SOURCE = document -> JsonUnflattener.unflattenAsMap(document.get(Constants.Fields.SOURCE_FIELD_NAME));

    public static final Function<Document, String> GET_ID = document -> document.get(Constants.Fields.ID_FIELD_NAME);

    public static final BiFunction<String, Map<String, Object>, Document> MAP_DOCUMENT = (id, source) -> {
        Document document = new Document();
        DocumentMapper.FIELDS.apply(source).forEach(document::add);
        document.add(new TermField(id));
        return document;
    };

}
