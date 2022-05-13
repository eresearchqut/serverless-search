package au.qut.edu.eresearch.serverlesssearch.service;

import io.quarkus.test.junit.QuarkusTestProfile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateUserPoolRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateUserPoolResponse;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class ApiKeyServiceTestProfile implements QuarkusTestProfile {

    public Map<String, String> getConfigOverrides() {

        GenericContainer cognitoLocal = new GenericContainer(DockerImageName.parse("jagregory/cognito-local:latest"))
                .withExposedPorts(9229);
        cognitoLocal.start();



        URI endpointOverride = URI.create(String.format("http://%s:%d", cognitoLocal.getHost(), cognitoLocal.getMappedPort(9229)));


        StaticCredentialsProvider staticCredentials = StaticCredentialsProvider
                .create(AwsBasicCredentials.create("local", "local"));


        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .endpointOverride(endpointOverride)
                .credentialsProvider(staticCredentials)
                .region(Region.of("local"))
                .build();


        CreateUserPoolResponse response = cognitoClient.createUserPool(
                CreateUserPoolRequest.builder()
                        .poolName("Test")
                        .build());


        Map<String, String> properties = new HashMap<>();
        properties.put("cognito.aws.endpoint-override", endpointOverride.toString());
        properties.put("cognito.aws.region", "local");
        properties.put("cognito.aws.credentials.type", "static");
        properties.put("cognito.aws.credentials.static-provider.access-key-id", "local");
        properties.put("cognito.aws.credentials.static-provider.secret-access-key", "local");
        properties.put("user-pool-id", response.userPool().id());
        properties.put("index.mount", "target/test-indexes/");
        return properties;
    }

}
