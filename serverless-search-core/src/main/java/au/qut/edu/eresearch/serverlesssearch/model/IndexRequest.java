package au.qut.edu.eresearch.serverlesssearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

@RegisterForReflection
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class IndexRequest {

    @JsonProperty("_index")
    private String index;

    @JsonProperty("_id")
    private String id;

    private Map<String, Object> document;



}
