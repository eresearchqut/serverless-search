package au.qut.edu.eresearch.serverlesssearch.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@RegisterForReflection
@Getter
@Builder
@EqualsAndHashCode
public class SearchResults {

    private long took;

    private Hits hits;

}
