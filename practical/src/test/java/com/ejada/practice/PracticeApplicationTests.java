package com.ejada.practice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

@SpringBootTest(properties = "spring.sql.init.mode=never")
/**
 * Integration tests for application startup.
 */
class PracticeApplicationTests {

	@MockitoBean
	private DataSource dataSource;

	@MockitoBean
	private JdbcTemplate jdbcTemplate;

	@Test
	void contextLoads() {
	}

}
