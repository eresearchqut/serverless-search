package au.qut.edu.eresearch.serverlesssearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@RegisterForReflection
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class GetRequest {


    @JsonProperty("_index")
    private String index;

    @JsonProperty("_id")
    private String id;


}
