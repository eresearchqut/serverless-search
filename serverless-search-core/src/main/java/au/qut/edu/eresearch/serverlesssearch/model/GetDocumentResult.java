package au.qut.edu.eresearch.serverlesssearch.model;

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
public class GetDocumentResult extends Document {

    private boolean found;
}
