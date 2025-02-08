package com.zlatenov.wedding_backend.repository;

import com.zlatenov.wedding_backend.TestContainerConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;

@DataJpaTest
public abstract class BaseRepositoryTest extends TestContainerConfig {

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