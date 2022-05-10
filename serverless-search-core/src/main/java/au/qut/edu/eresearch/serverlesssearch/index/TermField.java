package au.qut.edu.eresearch.serverlesssearch.index;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

public class TermField extends Field {

    public static final FieldType FIELD_TYPE = new FieldType();


    static {
        FIELD_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS); // not indexed
        FIELD_TYPE.setStored(true);
        FIELD_TYPE.setTokenized(false);
        FIELD_TYPE.setOmitNorms(true);
        FIELD_TYPE.freeze();
    }

    public TermField(String value) {
        super(Constants.Fields.ID_FIELD_NAME, value, FIELD_TYPE);
    }


}
