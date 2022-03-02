package io.quarkus.amazon.cognito.deployment;

import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CognitoIdentityProviderClientFullConfigTest {

    @Inject
    Instance<CognitoIdentityProviderClient> client;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addAsResource("sync-urlconn-full-config.properties", "application.properties"));

    @Test
    public void test() {
        assertNotNull(client.get());
        // should finish with success
    }
}
