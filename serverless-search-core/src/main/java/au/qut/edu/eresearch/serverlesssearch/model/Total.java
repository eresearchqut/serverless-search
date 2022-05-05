package au.qut.edu.eresearch.serverlesssearch.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@RegisterForReflection
@Getter
@Builder
@EqualsAndHashCode
@ToString
public class Total {

    private long value;

    private String relation;

}
