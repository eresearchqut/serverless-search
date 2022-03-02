package io.quarkus.amazon.cognito.deployment;

import io.quarkus.amazon.cognito.runtime.CognitoBuildTimeConfig;
import io.quarkus.amazon.cognito.runtime.CognitoClientProducer;
import io.quarkus.amazon.cognito.runtime.CognitoConfig;
import io.quarkus.amazon.cognito.runtime.CognitoRecorder;
import io.quarkus.amazon.common.deployment.*;
import io.quarkus.amazon.common.runtime.AmazonClientApacheTransportRecorder;
import io.quarkus.amazon.common.runtime.AmazonClientNettyTransportRecorder;
import io.quarkus.amazon.common.runtime.AmazonClientRecorder;
import io.quarkus.amazon.common.runtime.AmazonClientUrlConnectionTransportRecorder;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanRegistrationPhaseBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.ExtensionSslNativeSupportBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import org.jboss.jandex.DotName;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderAsyncClient;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderAsyncClientBuilder;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClientBuilder;

import java.util.List;

public class CognitoProcessor extends AbstractAmazonServiceProcessor {

    private static final String AMAZON_COGNITO = "amazon-cognito";

    CognitoBuildTimeConfig buildTimeConfig;

    @Override
    protected String amazonServiceClientName() {
        return AMAZON_COGNITO;
    }

    @Override
    protected String configName() {
        return "cognito";
    }

    @Override
    protected DotName syncClientName() {
        return DotName.createSimple(CognitoIdentityProviderClient.class.getName());
    }

    @Override
    protected DotName asyncClientName() {
        return DotName.createSimple(CognitoIdentityProviderAsyncClient.class.getName());
    }

    @Override
    protected String builtinInterceptorsPath() {
        return "software/amazon/awssdk/services/cognitoidentityprovider/execution.interceptors";
    }

    @BuildStep
    AdditionalBeanBuildItem producer() {
        return AdditionalBeanBuildItem.unremovableOf(CognitoClientProducer.class);
    }

    @BuildStep
    void setup(BeanRegistrationPhaseBuildItem beanRegistrationPhase,
            BuildProducer<ExtensionSslNativeSupportBuildItem> extensionSslNativeSupport,
            BuildProducer<FeatureBuildItem> feature,
            BuildProducer<AmazonClientInterceptorsPathBuildItem> interceptors,
            BuildProducer<AmazonClientBuildItem> clientProducer) {

        setupExtension(beanRegistrationPhase, extensionSslNativeSupport, feature, interceptors, clientProducer,
                buildTimeConfig.sdk,
                buildTimeConfig.syncClient);
    }

    @BuildStep(onlyIf = AmazonHttpClients.IsAmazonApacheHttpServicePresent.class)
    @Record(ExecutionTime.RUNTIME_INIT)
    void setupApacheSyncTransport(List<AmazonClientBuildItem> amazonClients, CognitoRecorder recorder,
            AmazonClientApacheTransportRecorder transportRecorder,
            BuildProducer<AmazonClientSyncTransportBuildItem> syncTransports) {

        createApacheSyncTransportBuilder(amazonClients,
                transportRecorder,
                buildTimeConfig.syncClient,
                recorder.getSyncConfig(),
                syncTransports);
    }

    @BuildStep(onlyIf = AmazonHttpClients.IsAmazonUrlConnectionHttpServicePresent.class)
    @Record(ExecutionTime.RUNTIME_INIT)
    void setupUrlConnectionSyncTransport(List<AmazonClientBuildItem> amazonClients, CognitoRecorder recorder,
            AmazonClientUrlConnectionTransportRecorder transportRecorder,
            BuildProducer<AmazonClientSyncTransportBuildItem> syncTransports) {

        createUrlConnectionSyncTransportBuilder(amazonClients,
                transportRecorder,
                buildTimeConfig.syncClient,
                recorder.getSyncConfig(),
                syncTransports);
    }

    @BuildStep(onlyIf = AmazonHttpClients.IsAmazonNettyHttpServicePresent.class)
    @Record(ExecutionTime.RUNTIME_INIT)
    void setupNettyAsyncTransport(List<AmazonClientBuildItem> amazonClients, CognitoRecorder recorder,
                                  AmazonClientNettyTransportRecorder transportRecorder,
                                  CognitoConfig runtimeConfig, BuildProducer<AmazonClientAsyncTransportBuildItem> asyncTransports) {

        createNettyAsyncTransportBuilder(amazonClients,
                transportRecorder,
                recorder.getAsyncConfig(),
                asyncTransports);
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void createClientBuilders(CognitoRecorder recorder,
                              AmazonClientRecorder commonRecorder,
                              List<AmazonClientSyncTransportBuildItem> syncTransports,
                              List<AmazonClientAsyncTransportBuildItem> asyncTransports,
                              BuildProducer<SyntheticBeanBuildItem> syntheticBeans) {

        createClientBuilders(commonRecorder,
                recorder.getAwsConfig(),
                recorder.getSdkConfig(),
                buildTimeConfig.sdk,
                syncTransports,
                asyncTransports,
                CognitoIdentityProviderClientBuilder.class,
                (syncTransport) -> recorder.createSyncBuilder(syncTransport),
                CognitoIdentityProviderAsyncClientBuilder.class,
                (asyncTransport) -> recorder.createAsyncBuilder(asyncTransport),
                null,
                null,
                syntheticBeans);
    }
}
