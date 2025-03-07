package vn.com.techcombank.lab_camuda_08_jobworker.configs;

import io.camunda.zeebe.client.CredentialsProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomOAuthConfiguration {
    @Bean
    public CredentialsProvider customOAuthCredentialsProvider() {
        String clientId = "your-access-token";
        String clientSecret = "internal";
        String channel = "";
        return new CustomHeaderCredentialsProvider(clientId, clientSecret, channel);
    }
}
