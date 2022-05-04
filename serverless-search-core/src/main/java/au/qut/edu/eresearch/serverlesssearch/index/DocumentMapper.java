package au.qut.edu.eresearch.serverlesssearch.index;

import com.github.wnameless.json.unflattener.JsonUnflattener;
import org.apache.lucene.document.Document;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DocumentMapper {


    public static final Function<Document, Map<String, Object>> GET_SOURCE = document -> JsonUnflattener.unflattenAsMap(document.get(SourceField.FIELD_NAME));

    public static final Function<Document, String> GET_ID = document -> document.get(IdField.FIELD_NAME);

    public static final BiFunction<String, Map<String, Object>, Document> MAP_DOCUMENT = (id, source) -> {
        Document document = new Document();
        FieldMapper.FIELDS.apply(source).forEach(document::add);
        document.add(new IdField(id));
        return document;
    };

}
