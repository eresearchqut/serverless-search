package au.qut.edu.eresearch.serverlesssearch.index;

import org.apache.lucene.index.IndexableField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FieldMapperTest {

    @Test
    public void emptySourceDocument() {
        Assertions.assertTrue(FieldMapper.FIELDS.apply(Map.of()).findAny().isEmpty());
    }

    @Test
    public void nullDocument() {
        Assertions.assertTrue(FieldMapper.FIELDS.apply(null).findAny().isEmpty());
    }


    @Test
    public void sourceField() {
        Map<String, Object> document = Map.of("a", "1");
        Assertions.assertFalse(FieldMapper.FIELDS.apply(document).filter(i -> i.name().equals("_source")).findAny().isEmpty());
        Assertions.assertEquals("{\"a\":\"1\"}", FieldMapper.FIELDS.apply(document).filter(i -> i.name() == "_source").findFirst().get().stringValue());
    }


    @Test
    public void stringField() {
        Map<String, Object> document = Map.of("b", "2");
        Assertions.assertFalse(FieldMapper.FIELDS.apply(document).filter(i -> i.name().equals("b")).findAny().isEmpty());
        Assertions.assertEquals("2", FieldMapper.FIELDS.apply(document).filter(i -> i.name().equals("b")).findFirst().get().stringValue());
    }

    @Test
    public void intField() {
        Map<String, Object> document = Map.of("c", Integer.valueOf(3));
        Assertions.assertFalse(FieldMapper.FIELDS.apply(document).filter(i -> i.name().equals("c")).findAny().isEmpty());
        Assertions.assertEquals(Integer.valueOf(3), FieldMapper.FIELDS.apply(document).filter(i -> i.name().equals("c")).findFirst().get().numericValue());
    }

    @Test
    public void longField() {
        Map<String, Object> document = Map.of("d", Long.valueOf(2147483648l));
        Assertions.assertFalse(FieldMapper.FIELDS.apply(document).filter(i -> i.name().equals("d")).findAny().isEmpty());
        Assertions.assertEquals(Long.valueOf(2147483648l), FieldMapper.FIELDS.apply(document).filter(i -> i.name().equals("d")).findFirst().get().numericValue());
    }

    @Test
    public void bigInteger() {
        Map<String, Object> document = Map.of("e", new BigInteger("243290200817664000098116788493"));
        Assertions.assertFalse(FieldMapper.FIELDS.apply(document).filter(i -> i.name().equals("e")).findAny().isEmpty());
        Assertions.assertEquals(new BigInteger("243290200817664000098116788493"), FieldMapper.FIELDS.apply(document).filter(i -> i.name().equals("e")).findFirst().get().numericValue());
    }

    @Test
    public void doubleField() {
        Map<String, Object> document = Map.of("g", 2.6578);
        Assertions.assertFalse(FieldMapper.FIELDS.apply(document).filter(i -> i.name().equals("g")).findAny().isEmpty());
        Assertions.assertEquals(Double.valueOf(2.6578), FieldMapper.FIELDS.apply(document).filter(i -> i.name().equals("g")).findFirst().get().numericValue());
    }

    @Test
    public void booleanField() {
        Map<String, Object> document = Map.of("h", true);
        Assertions.assertFalse(FieldMapper.FIELDS.apply(document).filter(i -> i.name().equals("h")).findAny().isEmpty());
        Assertions.assertEquals(Boolean.TRUE.toString(), FieldMapper.FIELDS.apply(document).filter(i -> i.name().equals("h")).findFirst().get().stringValue());
    }

    @Test
    public void nestedField() {
        Map<String, Object> document = Map.of("j", Map.of("k", 1, "l", 2));
        List<IndexableField> fields = FieldMapper.FIELDS.apply(document).collect(Collectors.toList());
        Assertions.assertFalse(FieldMapper.FIELDS.apply(document).filter(i -> i.name().equals("j.k")).findAny().isEmpty());
        Assertions.assertEquals(2, FieldMapper.FIELDS.apply(document).filter(i -> i.name().equals("j.l")).findFirst().get().numericValue());
    }

}
