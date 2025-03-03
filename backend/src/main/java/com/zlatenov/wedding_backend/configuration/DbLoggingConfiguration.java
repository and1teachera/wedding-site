package com.zlatenov.wedding_backend.configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@Profile("dev")
public class DbLoggingConfiguration {
    
    @Value("${spring.jpa.properties.hibernate.session.events.log.LOG_QUERIES_SLOWER_THAN_MS:500}")
    private long slowQueryThreshold;
    
    @PostConstruct
    public void logConfiguration() {
        log.info("Slow query logging enabled. Queries taking more than {}ms will be logged", slowQueryThreshold);
    }
}