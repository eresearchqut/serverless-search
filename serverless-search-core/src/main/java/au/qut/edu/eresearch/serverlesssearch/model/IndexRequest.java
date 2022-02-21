package au.qut.edu.eresearch.serverlesssearch.model;

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

    private String indexName;
    private Map<String, Object> document;
    private String id;


}
