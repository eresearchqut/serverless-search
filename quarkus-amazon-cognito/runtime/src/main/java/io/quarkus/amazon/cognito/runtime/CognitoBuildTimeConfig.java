package io.quarkus.amazon.cognito.runtime;

import io.quarkus.amazon.common.runtime.SdkBuildTimeConfig;
import io.quarkus.amazon.common.runtime.SyncHttpClientBuildTimeConfig;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

/**
 * Amazon Cognito build time configuration
 */
@ConfigRoot(name = "cognito", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class CognitoBuildTimeConfig {

    /**
     * SDK client configurations for AWS S3 client
     */
    @ConfigItem(name = ConfigItem.PARENT)
    public SdkBuildTimeConfig sdk;

    /**
     * Sync HTTP transport configuration for Amazon S3 client
     */
    @ConfigItem
    public SyncHttpClientBuildTimeConfig syncClient;
}
