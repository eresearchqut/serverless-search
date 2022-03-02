package io.quarkus.amazon.cognito.runtime;

import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderAsyncClient;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderAsyncClientBuilder;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClientBuilder;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;


@ApplicationScoped
public class CognitoClientProducer {
    private final CognitoIdentityProviderClient syncClient;
    private final CognitoIdentityProviderAsyncClient asyncClient;

    CognitoClientProducer(Instance<CognitoIdentityProviderClientBuilder> syncClientBuilderInstance, Instance<CognitoIdentityProviderAsyncClientBuilder> asyncClientBuilderInstance) {
        this.syncClient = syncClientBuilderInstance.isResolvable() ? syncClientBuilderInstance.get().build() : null;
        this.asyncClient = asyncClientBuilderInstance.isResolvable() ? asyncClientBuilderInstance.get().build() : null;
    }

    @Produces
    @ApplicationScoped
    public CognitoIdentityProviderClient client() {
        if (syncClient == null) {
            throw new IllegalStateException("The S3Client is required but has not been detected/configured.");
        }
        return syncClient;
    }

    @Produces
    @ApplicationScoped
    public CognitoIdentityProviderAsyncClient asyncClient() {
        if (asyncClient == null) {
            throw new IllegalStateException("The S3AsyncClient is required but has not been detected/configured.");
        }
        return asyncClient;
    }

    @PreDestroy
    public void destroy() {
        if (syncClient != null) {
            syncClient.close();
        }
        if (asyncClient != null) {
            asyncClient.close();
        }
    }
}
