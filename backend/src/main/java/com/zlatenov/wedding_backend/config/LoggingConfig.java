package com.zlatenov.wedding_backend.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.File;
import java.util.Arrays;

/**
 * Configuration class for logging setup
 */
@Configuration
public class LoggingConfig {

    private static final Logger log = LoggerFactory.getLogger(LoggingConfig.class);
    public static final String LOG_PATH = "LOG_PATH";

    private final Environment environment;
    
    public LoggingConfig(Environment environment) {
        this.environment = environment;
    }
    
    @PostConstruct
    public void init() {
        if (Arrays.asList(environment.getActiveProfiles()).contains("test")
                || environment.getActiveProfiles().length == 0) {
            setupTestLogging();
        } else {
            ensureLogDirectories();
        }
    }
    
    private void setupTestLogging() {
        File testLogDir = new File("src/test/resources/logs");
        
        // Clean up old test logs
        if (testLogDir.exists()) {
            File[] oldLogs = testLogDir.listFiles();
            if (oldLogs != null) {
                Arrays.stream(oldLogs).forEach(File::delete);
            }
        }
        
        if (!testLogDir.exists()) {
            boolean created = testLogDir.mkdirs();
            if (created) {
                log.info("Created test log directory at {}", testLogDir.getAbsolutePath());
            }
        }
        
        // Disable Loki for tests
        System.setProperty("logging.level.com.github.loki4j", "OFF");
    }
    
    private void ensureLogDirectories() {
        // Get log path from environment or use default
        String logPath = System.getProperty(LOG_PATH, "/var/log/wedding");
        
        // Create main log directory if it doesn't exist
        File logDir = new File(logPath);
        if (!logDir.exists()) {
            boolean created = logDir.mkdirs();
            if (created) {
                log.info("Created log directory at {}", logDir.getAbsolutePath());
            } else {
                log.warn("Failed to create log directory at {}. Logging to files may fail.", 
                        logDir.getAbsolutePath());
                
                File fallbackDir = new File("logs");
                if (!fallbackDir.exists()) {
                    if (fallbackDir.mkdirs()) {
                        log.info("Created fallback log directory at {}", fallbackDir.getAbsolutePath());
                        System.setProperty(LOG_PATH, fallbackDir.getAbsolutePath());
                    }
                } else {
                    System.setProperty(LOG_PATH, fallbackDir.getAbsolutePath());
                    log.info("Using fallback log directory at {}", fallbackDir.getAbsolutePath());
                }
            }
        }
    }
}