package au.qut.edu.eresearch.serverlesssearch.query;

import au.qut.edu.eresearch.serverlesssearch.index.TermQueryBuilder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.lucene.search.Query;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({@JsonSubTypes.Type(QueryStringQueryBuilder.class),
        @JsonSubTypes.Type(TermQueryBuilder.class),
        @JsonSubTypes.Type(BooleanQueryQueryBuilder.class),
        @JsonSubTypes.Type(MatchQueryBuilder.class),
        @JsonSubTypes.Type(MatchAllQueryBuilder.class),
        @JsonSubTypes.Type(MatchNoneQueryBuilder.class)})
public interface QueryBuilder {

    Query build();


}
