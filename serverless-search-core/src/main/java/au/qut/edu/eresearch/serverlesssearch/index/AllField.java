package au.qut.edu.eresearch.serverlesssearch.index;

import au.qut.edu.eresearch.serverlesssearch.Constants;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

public class AllField extends Field {

    public static final FieldType FIELD_TYPE = new FieldType();

    static {
        FIELD_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        FIELD_TYPE.setTokenized(true);
        FIELD_TYPE.freeze();
    }

    public AllField(String value) {
        super(Constants.Fields.ALL_FIELD_NAME, value, FIELD_TYPE);
    }


}
