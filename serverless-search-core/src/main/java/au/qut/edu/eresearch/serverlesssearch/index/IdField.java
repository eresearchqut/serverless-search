package au.qut.edu.eresearch.serverlesssearch.index;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.util.BytesRef;

public class IdField extends Field {

    public static final FieldType FIELD_TYPE = new FieldType();

    public static final String FIELD_NAME = "id";

    static {
        FIELD_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS); // not indexed
        FIELD_TYPE.setStored(true);
        FIELD_TYPE.setTokenized(false);
        FIELD_TYPE.setOmitNorms(true);
        FIELD_TYPE.freeze();
    }

    public IdField(String value) {
        super(FIELD_NAME, value, FIELD_TYPE);
    }


}
