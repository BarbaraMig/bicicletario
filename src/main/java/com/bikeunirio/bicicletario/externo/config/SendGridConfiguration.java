package com.bikeunirio.bicicletario.externo.config;

import com.sendgrid.SendGrid;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SendGridConfigurationProperties.class)
class SendGridConfiguration {
    private final SendGridConfigurationProperties sendGridConfigurationProperties;

    public SendGridConfiguration(SendGridConfigurationProperties sendGridConfigurationProperties) {
        this.sendGridConfigurationProperties = sendGridConfigurationProperties;
    }

    @Bean
    public SendGrid sendGrid() {
        String apiKey = sendGridConfigurationProperties.getApiKey();
        return new SendGrid(apiKey);
    }
}
