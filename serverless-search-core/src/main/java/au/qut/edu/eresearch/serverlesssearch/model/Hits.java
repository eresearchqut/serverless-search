package au.qut.edu.eresearch.serverlesssearch.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@RegisterForReflection
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class Hits {

    private Total total;

    private List<Hit> hits;

}
