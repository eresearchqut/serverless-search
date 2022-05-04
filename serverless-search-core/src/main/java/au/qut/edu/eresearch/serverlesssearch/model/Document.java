package au.qut.edu.eresearch.serverlesssearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@RegisterForReflection
@Getter
@SuperBuilder
@EqualsAndHashCode
@ToString
public class Document extends DocumentMetadata {

    @JsonProperty("_source")
    private Map<String, Object> source;

}
Added missing