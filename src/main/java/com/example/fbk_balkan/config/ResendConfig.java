package com.example.fbk_balkan.config;

import com.resend.Resend;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResendConfig {

    @Value("${resend.api.key}")
    private String apiKey;

    @Bean
    public Resend resendClient() {
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("WARNING: RESEND_API_KEY is not set!");
        }
        return new Resend(apiKey);
    }
}



