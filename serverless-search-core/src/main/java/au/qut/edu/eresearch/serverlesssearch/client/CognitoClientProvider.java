package au.qut.edu.eresearch.serverlesssearch.client;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.net.URI;
import java.util.Optional;

@ApplicationScoped
public class CognitoClientProvider {

    @ConfigProperty(name = "cognito.aws.region")
    Optional<String> awsRegion;

    @ConfigProperty(name = "cognito.aws.endpoint-override")
    Optional<String> endpointOverride;

    @Produces
    public CognitoIdentityProviderClient getCognitoIdentityProviderClient() {
        return CognitoIdentityProviderClient
                .builder()
                .region(awsRegion.map(awsRegion -> Region.of(awsRegion)).orElse(null))
                .endpointOverride(endpointOverride.map(endpointOverride -> URI.create(endpointOverride)).orElse(null))
                .build();
    }
}
