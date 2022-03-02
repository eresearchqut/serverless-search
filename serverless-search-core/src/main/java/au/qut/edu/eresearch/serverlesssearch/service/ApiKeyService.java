package au.qut.edu.eresearch.serverlesssearch.service;

import au.qut.edu.eresearch.serverlesssearch.model.ApiKey;
import au.qut.edu.eresearch.serverlesssearch.model.ApiKeyRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateUserPoolClientRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateUserPoolClientResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


@ApplicationScoped
public class ApiKeyService {


    @ConfigProperty(name = "user-pool-id")
    String userPoolId;

    @Inject
    CognitoIdentityProviderClient cognitoClient;

    public ApiKey createKey(ApiKeyRequest apiKeyRequest) {
        CreateUserPoolClientResponse response = cognitoClient.createUserPoolClient(
                CreateUserPoolClientRequest.builder()
                        .clientName(apiKeyRequest.getClientName())
                        .userPoolId(userPoolId)
                        .generateSecret(true)
                        .allowedOAuthScopes(apiKeyRequest.getScopes())
                        .build());

        return new ApiKey()
                .setClientId(response.userPoolClient().clientId())
                .setClientSecret(response.userPoolClient().clientSecret())
                .setClientName(response.userPoolClient().clientName())
                .setScopes(response.userPoolClient().allowedOAuthScopes());


    }


}
