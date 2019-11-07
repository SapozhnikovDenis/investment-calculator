package com.sapozhnikov.investment.calculator.configuration.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class WebConfiguration {

    @Bean
    public RestTemplate externalSystemRestTemplate(
            @Value("${external.system.timeout.millis.connect}") Long millisConnectTimeout,
            @Value("${external.system.timeout.millis.read}") Long millisReadTimeout) {
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(millisConnectTimeout))
                .setReadTimeout(Duration.ofMillis(millisReadTimeout))
                .build();
    }

}
