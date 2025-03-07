package vn.com.techcombank.lab_camuda_08_jobworker.configs;

import io.camunda.zeebe.client.CredentialsProvider;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;

@ThreadSafe
public class CustomHeaderCredentialsProvider implements CredentialsProvider {

    private final String muleClientId;
    private final String muleClientSecret;
    private final String muleChannel;

    public CustomHeaderCredentialsProvider(String muleClientId, String muleClientSecret, String muleChannel) {
        this.muleClientId = muleClientId;
        this.muleClientSecret = muleClientSecret;
        this.muleChannel = muleChannel;
    }

    @Override
    public void applyCredentials(CredentialsApplier applier) throws IOException {

    }

    @Override
    public boolean shouldRetryRequest(StatusCode statusCode) {
        return false;
    }
}
