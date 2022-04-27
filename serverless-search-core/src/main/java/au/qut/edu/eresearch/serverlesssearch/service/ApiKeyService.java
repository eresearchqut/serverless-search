package au.qut.edu.eresearch.serverlesssearch.service;

import au.qut.edu.eresearch.serverlesssearch.model.ApiKey;
import au.qut.edu.eresearch.serverlesssearch.model.ApiKeyRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
import software.amazon.awssdk.services.cognitoidentityprovider.paginators.ListUserPoolClientsIterable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;


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

    public ApiKey getKey(String clientId) {
        DescribeUserPoolClientResponse response = cognitoClient.describeUserPoolClient(
                DescribeUserPoolClientRequest.builder()
                        .userPoolId(userPoolId)
                        .clientId(clientId)
                        .build());

        return new ApiKey()
                .setClientId(response.userPoolClient().clientId())
                .setClientSecret(response.userPoolClient().clientSecret())
                .setClientName(response.userPoolClient().clientName())
                .setScopes(response.userPoolClient().allowedOAuthScopes());


    }

    public void deleteKey(String clientId) {
        cognitoClient.deleteUserPoolClient(
                DeleteUserPoolClientRequest.builder()
                        .userPoolId(userPoolId)
                        .clientId(clientId)
                        .build());
    }

    public List<ApiKey> listKeys() {
        ListUserPoolClientsIterable response = cognitoClient.listUserPoolClientsPaginator(
                ListUserPoolClientsRequest.builder()
                        .userPoolId(userPoolId)
                        .build());
        return response.userPoolClients().stream()
                .map(userPoolClient -> new ApiKey()
                        .setClientId(userPoolClient.clientId())
                        .setClientName(userPoolClient.clientName()))
                .collect(Collectors.toList());
    }


}
