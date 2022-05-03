package au.qut.edu.eresearch.serverlesssearch.index;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class DocumentMapperTest {

    @Test
    public void emptySourceDocument() {
        Assertions.assertTrue(FieldMapper.FIELD_STREAM.apply(Map.of()).findAny().isEmpty());
    }

    @Test
    public void nullDocument() {
        Assertions.assertTrue(FieldMapper.FIELD_STREAM.apply(null).findAny().isEmpty());
    }

    @Test
    public void sourceField() {
        Map<String, Object> document = Map.of("a", "1");
        Assertions.assertFalse(FieldMapper.FIELD_STREAM.apply(document).filter(i -> i.name() == "_source").findAny().isEmpty());
        Assertions.assertEquals("{\"a\":\"1\"}", FieldMapper.FIELD_STREAM.apply(document).filter(i -> i.name() == "_source").findFirst().get().stringValue());
    }


}
