package com.zlatenov.wedding_backend.repository;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test")
@Testcontainers
@DataJpaTest
public abstract class BaseRepositoryTest {

    private static final MariaDBContainer<?> mariaDBContainer;

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