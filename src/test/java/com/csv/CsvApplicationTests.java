package com.csv;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CsvApplicationTests extends AbstractIntegrationTest {

	@Test
	void contextLoads() {
        System.out.println("Banco esta rodando: " + postgres.isRunning());
        assertTrue(postgres.isRunning());
	}

}
