package au.qut.edu.eresearch.serverlesssearch.service;


import au.qut.edu.eresearch.serverlesssearch.model.ApiKey;
import au.qut.edu.eresearch.serverlesssearch.model.ApiKeyRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;

@QuarkusTest
@TestProfile(ServiceTestProfile.class)
public class ApiKeyServiceTest {


    @Inject
    ApiKeyService apiKeyService;

    @Test
    public void lifecycle() {
        ApiKey apiKey = apiKeyService
                .createKey(new ApiKeyRequest().setClientName("Client").setScopes(List.of("index/put")));
        Assertions.assertEquals("Client", apiKey.getClientName());
        Assertions.assertNotNull("Client", apiKey.getClientName());

        ApiKey retrieved = apiKeyService.getKey(apiKey.getClientId());
        Assertions.assertEquals("Client", retrieved.getClientName());
        Assertions.assertEquals(apiKey.getClientId(), retrieved.getClientId());

    }


}



