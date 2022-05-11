package au.qut.edu.eresearch.serverlesssearch.index;

import au.qut.edu.eresearch.serverlesssearch.Constants;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

public class SourceField extends Field {

    public static final FieldType FIELD_TYPE = new FieldType();

    static {
        FIELD_TYPE.setIndexOptions(IndexOptions.NONE); // not indexed
        FIELD_TYPE.setStored(true);
        FIELD_TYPE.setOmitNorms(true);
        FIELD_TYPE.freeze();
    }

    public SourceField(String value) {
        super(Constants.Fields.SOURCE_FIELD_NAME, value, FIELD_TYPE);
    }


}
