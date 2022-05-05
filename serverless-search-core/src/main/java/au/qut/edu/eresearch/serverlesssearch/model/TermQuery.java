package au.qut.edu.eresearch.serverlesssearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@RegisterForReflection
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class TermQuery {

    private String query;

    @JsonProperty("default_field")
    private String defaultField;

    private String analyzer;


}
