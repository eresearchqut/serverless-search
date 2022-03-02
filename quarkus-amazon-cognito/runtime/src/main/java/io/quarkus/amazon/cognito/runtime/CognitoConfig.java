package io.quarkus.amazon.cognito.runtime;

import io.quarkus.amazon.common.runtime.AwsConfig;
import io.quarkus.amazon.common.runtime.NettyHttpClientConfig;
import io.quarkus.amazon.common.runtime.SdkConfig;
import io.quarkus.amazon.common.runtime.SyncHttpClientConfig;
import io.quarkus.runtime.annotations.ConfigDocSection;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

import java.util.Optional;

@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public class CognitoConfig {





    /**
     * Define the profile name that should be consulted to determine the default value of {@link #useArnRegionEnabled}.
     * This is not used, if the {@link #useArnRegionEnabled} is configured to 'true'.
     * <p>
     * If not specified, the value in `AWS_PROFILE` environment variable or `aws.profile` system property is used and
     * defaults to `default` name.
     */
    @ConfigItem
    public Optional<String> profileName;

    /**
     * AWS SDK client configurations
     */
    @ConfigItem(name = ConfigItem.PARENT)
    @ConfigDocSection
    public SdkConfig sdk;

    /**
     * AWS services configurations
     */
    @ConfigItem
    @ConfigDocSection
    public AwsConfig aws;

    /**
     * Sync HTTP transport configurations
     */
    @ConfigItem
    @ConfigDocSection
    public SyncHttpClientConfig syncClient;

    /**
     * Netty HTTP transport configurations
     */
    @ConfigItem
    @ConfigDocSection
    public NettyHttpClientConfig asyncClient;
}
