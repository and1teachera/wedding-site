package com.zlatenov.wedding_backend.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Jackson JSON serialization options
 */
@Slf4j
@Configuration
public class JacksonConfig {
    
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        log.info("Configuring custom Jackson settings");
        return builder -> {
            builder.featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            builder.featuresToEnable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            log.info("Jackson customization applied");
        };
    }
}