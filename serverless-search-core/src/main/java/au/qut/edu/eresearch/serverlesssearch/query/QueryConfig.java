package au.qut.edu.eresearch.serverlesssearch.query;

import au.qut.edu.eresearch.serverlesssearch.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.NoArgsConstructor;

@RegisterForReflection
@Data
@NoArgsConstructor
public class QueryConfig {

    private String query;

    private String field;

    private int slop;

    @JsonProperty(Constants.Query.ANALYZER_ATTRIBUTE_NAME)
    private String analyzerName = Constants.Analysers.STANDARD_ANALYZER_NAME;
}
