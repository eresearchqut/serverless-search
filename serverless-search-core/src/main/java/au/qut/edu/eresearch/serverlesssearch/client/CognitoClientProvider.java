package au.qut.edu.eresearch.serverlesssearch.client;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.net.URI;
import java.util.Optional;

@ApplicationScoped
public class CognitoClientProvider {

    @ConfigProperty(name = "cognito.aws.region")
    String awsRegion;

    @ConfigProperty(name = "cognito.aws.endpoint-override")
    String endpointOverride;

    @ConfigProperty(name = "cognito.aws.credentials.type")
    String credentialsType;

    @ConfigProperty(name = "cognito.aws.credentials.static-provider.access-key-id")
    String accessKeyId;

    @ConfigProperty(name = "cognito.aws.credentials.static-provider.secret-access-key")
    String secretAccessKey;

    @Produces
    public CognitoIdentityProviderClient getCognitoIdentityProviderClient() {
        return CognitoIdentityProviderClient.builder()
                .credentialsProvider("static".equalsIgnoreCase(credentialsType) ? StaticCredentialsProvider
                        .create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)) : null)
                .region(Optional.ofNullable(awsRegion).map(awsRegion -> Region.of(awsRegion)).orElse(null))
                .endpointOverride(Optional.ofNullable(endpointOverride).map(endpointOverride -> URI.create(endpointOverride)).orElse(null))
                .build();
    }
}
