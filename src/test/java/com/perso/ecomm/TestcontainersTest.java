package com.perso.ecomm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
public class TestcontainersTest {

    @Container
    private static final MySQLContainer<?> mySQLContainer =
            new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("ecommerce")
                    .withUsername("root")
                    .withPassword("root");

    @DynamicPropertySource
    private static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
    }

    @Test
    void canStartMysqlDB() {
        assertThat(mySQLContainer.isRunning()).isTrue();
        assertThat(mySQLContainer.isCreated()).isTrue();
    }
}
