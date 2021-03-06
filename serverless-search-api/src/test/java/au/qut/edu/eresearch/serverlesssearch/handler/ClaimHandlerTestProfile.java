package au.qut.edu.eresearch.serverlesssearch.handler;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.HashMap;
import java.util.Map;

public class ClaimHandlerTestProfile implements QuarkusTestProfile {

    public Map<String, String> getConfigOverrides() {
        Map<String, String> properties = new HashMap<>();
        properties.put("queue.url", "not.used");
        properties.put("index.mount", "not.used");
        properties.put("user-pool-id", "not.used");
        return properties;
    }

}
