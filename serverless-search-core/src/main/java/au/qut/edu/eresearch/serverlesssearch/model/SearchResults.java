package au.qut.edu.eresearch.serverlesssearch.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@RegisterForReflection
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class SearchResults {

    private long took;

    private Hits hits = new Hits();



}
