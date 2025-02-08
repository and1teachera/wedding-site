package com.zlatenov.wedding_backend;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * @author Angel Zlatenov
 */

@Testcontainers
@ActiveProfiles("test")
public class TestContainerConfig {
    protected static final MariaDBContainer<?> mariaDBContainer;

    static {
        mariaDBContainer = new MariaDBContainer<>("mariadb:10.5")
                .withDatabaseName("wedding_test")
                .withUsername("test_user")
                .withPassword("test_pass");
        mariaDBContainer.start();
    }

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariaDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDBContainer::getUsername);
        registry.add("spring.datasource.password", mariaDBContainer::getPassword);
    }
}
