package com.ejada.practice.dayfour.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI employeeApiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Employee CRUD API")
                        .version("1.0.0")
                        .description("Simple Spring Boot practice project: REST CRUD over Oracle "
                                + "using plain JDBC (JdbcTemplate), a global exception handler, "
                                + "and an AOP logging aspect on the service layer.")
                        .contact(new Contact().name("Ejada Practice Project"))
                        .license(new License().name("Internal use / practice project")));
    }
}
