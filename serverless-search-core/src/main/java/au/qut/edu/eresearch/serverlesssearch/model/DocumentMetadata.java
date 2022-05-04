package au.qut.edu.eresearch.serverlesssearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@RegisterForReflection
@Getter
@SuperBuilder
@EqualsAndHashCode
@ToString
public class DocumentMetadata {

    @JsonProperty("_index")
    private String index;

    @JsonProperty("_id")
    private String id;


}
