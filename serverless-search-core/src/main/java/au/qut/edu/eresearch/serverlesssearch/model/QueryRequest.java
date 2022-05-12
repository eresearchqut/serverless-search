package au.qut.edu.eresearch.serverlesssearch.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@RegisterForReflection
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class QueryRequest {

    private String index;

    private Integer size;

    private Integer from;

    private Map<String, Object> query;

    private List<Map<String, Map<String, String>>> sort;


}
