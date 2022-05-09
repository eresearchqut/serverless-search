package au.qut.edu.eresearch.serverlesssearch.index;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.lucene.search.Query;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({ @JsonSubTypes.Type(QueryStringQueryBuilder.class), @JsonSubTypes.Type(TermQueryBuilder.class) })
public interface QueryBuilder {

    public Query build() ;


}
