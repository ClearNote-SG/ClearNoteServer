package com.example.clearnote.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
@Configuration
public class ClovaConfig {

    @Value("${clova.api.key}")
    private String clovaApiKey;
    @Bean(name = "clovaTemplate")
    public RestTemplate clovaRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("X-CLOVASPEECH-API-KEY", clovaApiKey);
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}
