package com.bikeunirio.bicicletario.externo.config;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "com.externo.sendgrid")
public class SendGridConfigurationProperties {

    @NotBlank
    @Pattern(regexp = "^SG\\.[0-9A-Za-z._-]{60,}$")
    private String apiKey;

    @Email
    private final String fromEmail = "emailexternoes2@gmail.com";

    private final String fromName = "BikeUnirio";

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public String getFromName() {
        return fromName;
    }
}
