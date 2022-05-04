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
@EqualsAndHashCode(callSuper = true)
@ToString
public class Hit extends Document {

    @JsonProperty("_score")
    private float score;
}
